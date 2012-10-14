/**
 * Copyright (C) 2012 White Source Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.whitesource.teamcity.agent;

import jetbrains.buildServer.agent.BuildRunnerContext;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

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
