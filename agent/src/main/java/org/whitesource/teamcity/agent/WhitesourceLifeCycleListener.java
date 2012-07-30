package org.whitesource.teamcity.agent;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.agent.api.model.Coordinates;
import org.whitesource.agent.api.model.DependencyInfo;
import org.whitesource.teamcity.common.ChecksumUtils;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "before runner start " + runner.getBuild().getProjectName() + " type " + runner.getName()));

        if (!shouldUpdate(runner)) {
            return; // no need to update white source...
        }

    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        super.runnerFinished(runner, status);
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "runner finished " + runner.getBuild().getProjectName() + " type " + runner.getName()));

        if (!shouldUpdate(runner)) {
            return; // no need to update white source...
        }

        runner.getBuild().getBuildLogger().message("Updating White Source");

        // collect OSS usage information
        runner.getBuild().getBuildLogger().message("Collecting OSS usage information");
        Collection<AgentProjectInfo> projectInfos = new ArrayList<AgentProjectInfo>();
        if (WssUtils.isMavenRunType(runner.getRunType())) {
            projectInfos = doMaven(runner);
        }
        dumpAgentProjectInfos(projectInfos);

        // send to white source
        runner.getBuild().getBuildLogger().message("Sending to White Source");
        projectInfos.size();

        runner.getBuild().getBuildLogger().message("Successfully update White Source.");
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
            throw new RuntimeException("Error collecting maven information. Skipping update.");
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
        String shouldUpdate = runner.getRunnerParameters().get(Constants.UPDATE_WHITESOURCE);
        return StringUtil.isEmptyOrSpaces(shouldUpdate) || !Boolean.valueOf(shouldUpdate);
    }

    private void dumpAgentProjectInfos(Collection<AgentProjectInfo> projectInfos) {
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
