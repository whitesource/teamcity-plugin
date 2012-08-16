package org.whitesource.teamcity.agent;

import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.StringUtil;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for extractors of open source usage information;
 *
 * @author Edo.Shor
 */
public abstract class BaseOssInfoExtractor {

    /* --- Members --- */

    protected BuildRunnerContext runner;

    protected String projectToken;

    protected List<String> includes;

    protected List<String> excludes;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param runner
     */
    protected BaseOssInfoExtractor(BuildRunnerContext runner) {
        this.runner = runner;

        projectToken = runner.getRunnerParameters().get(Constants.RUNNER_PROJECT_TOKEN);
        includes = WssUtils.splitParameters(
                runner.getRunnerParameters().get(Constants.RUNNER_INCLUDES));
        excludes = WssUtils.splitParameters(
                runner.getRunnerParameters().get(Constants.RUNNER_EXCLUDES));
    }

    /* --- Abstract methods --- */

    public abstract Collection<AgentProjectInfo> extract();

}
