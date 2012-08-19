package org.whitesource.teamcity.agent;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import org.whitesource.agent.api.dispatch.UpdateInventoryResult;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.agent.api.model.DependencyInfo;
import org.whitesource.api.client.WhitesourceService;
import org.whitesource.api.client.WssServiceException;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

import java.util.Collection;

/**
 * @author Edo.Shor
 */
public class WhitesourceLifeCycleListener extends AgentLifeCycleAdapter {

    /* --- Static members --- */

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

        if (shouldUpdate(runner)) {
            Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "before runner start "
                    + runner.getBuild().getProjectName() + " type " + runner.getName()));
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

        // make sure we have an organization token
        String orgToken = runner.getRunnerParameters().get(Constants.RUNNER_OVERRIDE_ORGANIZATION_TOKEN);
        if (StringUtil.isEmptyOrSpaces(orgToken)) {
            orgToken = runner.getRunnerParameters().get(Constants.RUNNER_ORGANIZATION_TOKEN);
        }
        if (StringUtil.isEmptyOrSpaces(orgToken)) {
            stopBuild(runner, new IllegalStateException("Empty organization token. " +
                    "Please make sure an organization token is defined for this runner"));
            return;
        }

        // collect OSS usage information
        buildLogger.message("Collecting OSS usage information");
        BaseOssInfoExtractor extractor = null;
        if (WssUtils.isMavenRunType(runner.getRunType())) {
            extractor = new MavenOssInfoExtractor(runner);
        } else {
            extractor = new GenericOssInfoExtractor(runner);
        }
        Collection<AgentProjectInfo> projectInfos = extractor.extract();
        debugAgentProjectInfos(projectInfos);

        // send to white source
        if (CollectionUtils.isEmpty(projectInfos)) {
            buildLogger.message("No open source information found.");
        } else {
            buildLogger.message("Sending to White Source");
            WhitesourceService service = createServiceClient(runner);
            try{
                final UpdateInventoryResult updateResult = service.update(orgToken, projectInfos);
                logUpdateResult(updateResult, buildLogger);
                buildLogger.message("Successfully updated White Source.");
            } catch (WssServiceException e) {
                stopBuild(runner, e);
            } finally {
                service.shutdown();
            }
        }
    }

    /* --- Private methods --- */

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
            log.info("Project coordiantes: " + projectInfo.getCoordinates());
            log.info("Project parent coordiantes: " + projectInfo.getParentCoordinates());
            log.info("total # of dependencies: " + projectInfo.getDependencies().size());
            for (DependencyInfo info :  projectInfo.getDependencies()) {
                log.info(info + " SHA-1: " + info.getSha1());
            }
        }
        log.info("----------------- dump finished -----------------");

    }
}
