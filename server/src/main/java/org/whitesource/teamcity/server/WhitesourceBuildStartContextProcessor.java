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
package org.whitesource.teamcity.server;

import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.serverSide.SRunnerContext;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import org.jetbrains.annotations.NotNull;
import org.whitesource.teamcity.common.Constants;

/**
 * Implementation of the interface for loading plugin settings into new builds.
 *
 * @author Edo.Shor
 */
public class WhitesourceBuildStartContextProcessor implements BuildStartContextProcessor {

    /* --- Members -- */

    private GlobalSettingsManager settingsManager;

    /* --- Constructors -- */

    /**
     * Constructor
     *
     * @param settingsManager
     */
    public WhitesourceBuildStartContextProcessor(@NotNull final GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void updateParameters(@NotNull BuildStartContext context) {

        GlobalSettings globalSettings = settingsManager.getGlobalSettings();
        if (globalSettings == null) {
            return;
        }

        String orgToken = globalSettings.getOrgToken();
        boolean checkPolicies = globalSettings.isCheckPolicies();
        ProxySettings proxy = globalSettings.getProxy();

        for (SRunnerContext runnerContext : context.getRunnerContexts()) {
            // system envrionment
            runnerContext.addRunnerParameter(Constants.RUNNER_SERVICE_URL,
                    TeamCityProperties.getProperty(Constants.RUNNER_SERVICE_URL));

            // global settings
            runnerContext.addRunnerParameter(Constants.RUNNER_ORGANIZATION_TOKEN, orgToken);
            runnerContext.addRunnerParameter(Constants.RUNNER_CHECK_POLICIES, Boolean.toString(checkPolicies));

            if (proxy != null) {
                runnerContext.addRunnerParameter(Constants.RUNNER_PROXY_HOST, proxy.getHost());
                runnerContext.addRunnerParameter(Constants.RUNNER_PROXY_PORT, Integer.valueOf(proxy.getPort()).toString());
                runnerContext.addRunnerParameter(Constants.RUNNER_PROXY_USERNAME, proxy.getUsername());
                runnerContext.addRunnerParameter(Constants.RUNNER_PROXY_PASSWORD, proxy.getPassword());
            }
        }

    }

}
