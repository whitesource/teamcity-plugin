package org.whitesource.teamcity.agent;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.whitesource.agent.api.ChecksumUtils;
import org.whitesource.agent.api.dispatch.UpdateInventoryResult;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.agent.api.model.Coordinates;
import org.whitesource.agent.api.model.DependencyInfo;
import org.whitesource.api.client.ClientConstants;
import org.whitesource.api.client.WhitesourceService;
import org.whitesource.api.client.WssServiceException;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Edo.Shor
 */
public class WhitesourceLifeCycleListener extends AgentLifeCycleAdapter {

    /* --- Static members --- */

    public static final String MAVEN_BUILD_INFO_XML = "maven-build-info.xml";

    private static final String LOG_COMPONENT = "LifeCycleListener";

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param eventDispatcher
     */
    public WhitesourceLifeCycleListener(@NotNull final EventDispatcher<AgentLifeCycleListener> eventDispatcher) {
        eventDispatcher.addListener(this);
    }

    @Override
    public void agentInitialized(@NotNull BuildAgent agent) {
        super.agentInitialized(agent);
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "initialized"));
    }

    /* --- Interface implementation methods --- */

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        super.beforeRunnerStart(runner);

        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "before runner start "
                + runner.getBuild().getProjectName() + " type " + runner.getName()));

        if (!shouldUpdate(runner)) {
            return; // no need to update white source...
        }
    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        super.runnerFinished(runner, status);
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "runner finished "
                + runner.getBuild().getProjectName() + " type " + runner.getName()));

        if (!shouldUpdate(runner)) {
            return; // no need to update white source...
        }

        final BuildProgressLogger buildLogger = runner.getBuild().getBuildLogger();
        buildLogger.message("Updating White Source");

        // collect OSS usage information
        buildLogger.message("Collecting OSS usage information");
        Collection<AgentProjectInfo> projectInfos = new ArrayList<AgentProjectInfo>();
        if (WssUtils.isMavenRunType(runner.getRunType())) {
            projectInfos = doMaven(runner);
        }
        debugAgentProjectInfos(projectInfos);

        // send to white source
        buildLogger.message("Sending to White Source");
        WhitesourceService service = createServiceClient(runner);
        try{
            final String orgToken = runner.getRunnerParameters().get(Constants.RUNNER_ORGANIZATION_TOKEN);
            final UpdateInventoryResult updateResult = service.update(orgToken, projectInfos);
            logUpdateResult(updateResult, buildLogger);
            buildLogger.message("Successfully update White Source.");
        } catch (WssServiceException e) {
            stopBuild(runner, e);
        } finally {
            service.shutdown();
        }
    }

    /* --- Private methods --- */

    private Collection<AgentProjectInfo>  doMaven(BuildRunnerContext runner) {
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "Collecting maven information"));

        Collection<AgentProjectInfo> projectInfos = new ArrayList<AgentProjectInfo>();

        final AgentRunningBuild build = runner.getBuild();

        // find and maven build info report
        File mavenBuildInfoFile = new File(build.getBuildTempDirectory(), MAVEN_BUILD_INFO_XML);
        if (!mavenBuildInfoFile.exists()) {
            String missingMavenInfo = "Can't find maven build info report.";
            Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, missingMavenInfo));
            build.getBuildLogger().warning(missingMavenInfo);
            throw new RuntimeException("Error collecting maven information, Skipping update.");
        }

        // parse maven info report and extract OSS usage information
        try {
            Element root = FileUtil.parseDocument(mavenBuildInfoFile);

            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, xmlOutputter.outputString(root)));

            Element projects = root.getChild("projects");
            if (projects != null) {
                List<Element> projectList = projects.getChildren("project");
                for (Element projectElement : projectList) {
                    AgentProjectInfo projectInfo = new AgentProjectInfo();

                    String groupId = projectElement.getChildText("groupId");
                    String artifactId = projectElement.getChildText("artifactId");
                    String version = projectElement.getChildText("version");
                    projectInfo.setCoordinates(new Coordinates(groupId, artifactId, version));

                    Element dependencyArtifacts = projectElement.getChild("dependencyArtifacts");
                    if (dependencyArtifacts != null) {
                        List<Element> dependencyList = dependencyArtifacts.getChildren("artifact");
                        for (Element dependencyElement : dependencyList) {
                            DependencyInfo info = new DependencyInfo();

                            info.setGroupId(projectElement.getChildText("groupId"));
                            info.setArtifactId(projectElement.getChildText("artifactId"));
                            info.setVersion(projectElement.getChildText("version"));
                            info.setClassifier(projectElement.getChildText("classifier"));
                            info.setType(projectElement.getChildText("type"));
                            info.setScope(projectElement.getChildText("scope"));

                            String dependencyPath = dependencyElement.getChildText("path");
                            if (!StringUtil.isEmptyOrSpaces(dependencyPath)) {
                                info.setSystemPath(dependencyPath);
                                try {
                                    info.setSha1(ChecksumUtils.calculateSHA1(new File(dependencyPath)));
                                } catch (IOException e) {
                                    Loggers.AGENT.debug("Unable to calculate SHA-1 for " + dependencyPath);
                                }
                            }

                            projectInfo.getDependencies().add(info);
                        }
                    }
                    projectInfos.add(projectInfo);
                }
            }
        } catch (JDOMException e) {
            throw new RuntimeException("Error parsing maven information. Skipping update.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading maven information. Skipping update.", e);
        }

        return projectInfos;
    }

    private boolean shouldUpdate(BuildRunnerContext runner) {
        String shouldUpdate = runner.getRunnerParameters().get(Constants.RUNNER_DO_UPDATE);
        return !StringUtil.isEmptyOrSpaces(shouldUpdate) && Boolean.valueOf(shouldUpdate);
    }

    private WhitesourceService createServiceClient(BuildRunnerContext runner) {
        String serviceUrl = runner.getRunnerParameters().get(Constants.RUNNER_SERVICE_URL);
        WhitesourceService service = new WhitesourceService(Constants.AGENT_TYPE, Constants.AGENT_VERSION, serviceUrl);

        String proxyHost = runner.getRunnerParameters().get(Constants.RUNNER_PROXY_HOST);
        if (!StringUtil.isEmptyOrSpaces(proxyHost)) {
            int port = Integer.parseInt(runner.getRunnerParameters().get(Constants.RUNNER_PROXY_PORT));
            String username = runner.getRunnerParameters().get(Constants.RUNNER_PROXY_USERNAME);
            String password = runner.getRunnerParameters().get(Constants.RUNNER_PROXY_PASSWORD);
            service.getClient().setProxy(proxyHost, port, username, password);
        }

        return service;
    }

    private void logUpdateResult(UpdateInventoryResult result, BuildProgressLogger logger) {
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "update success"));

        logger.message("White Source update results: ");
        logger.message("White Source organization: " + result.getOrganization());
        logger.message(result.getCreatedProjects().size() + " Newly created projects:");
        StringUtil.join(result.getCreatedProjects(), ",");
        logger.message(result.getUpdatedProjects().size() + " existing projects were updated:");
        StringUtil.join(result.getUpdatedProjects(), ",");
    }

    private void stopBuild(BuildRunnerContext runner, Exception e) {
        Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, "Stopping build"), e);

        BuildProgressLogger logger = runner.getBuild().getBuildLogger();
        String errorMessage = e.getLocalizedMessage();
        logger.buildFailureDescription(errorMessage);
        logger.exception(e);
        logger.flush();
        ((AgentRunningBuildEx) runner.getBuild()).stopBuild(errorMessage);
    }

    private void debugAgentProjectInfos(Collection<AgentProjectInfo> projectInfos) {
        final Logger log = Loggers.AGENT;

        log.info("----------------- dumping projectInfos -----------------");
        log.info("Total number of projects : " + projectInfos.size());
        for (AgentProjectInfo projectInfo : projectInfos) {
            log.info("Project coordiantes: " + projectInfo.getCoordinates().toString());
            log.info("total # of dependencies: " + projectInfo.getDependencies().size());
            for (DependencyInfo info :  projectInfo.getDependencies()) {
                log.info(info.toString() + " SHA-1: " + info.getSha1());
            }
        }
        log.info("----------------- dump finished -----------------");

    }
}
