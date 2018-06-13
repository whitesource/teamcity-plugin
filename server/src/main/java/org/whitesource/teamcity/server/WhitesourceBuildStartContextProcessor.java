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
     */
    public WhitesourceBuildStartContextProcessor(@NotNull final GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    /* --- Interface implementation methods --- */

    @Override
    public void updateParameters(@NotNull BuildStartContext context) {
        GlobalSettings globalSettings = settingsManager.getGlobalSettings();
        if (globalSettings == null) { return; }

        String orgToken = globalSettings.getOrgToken();
        String userKey = globalSettings.getUserKey();
        String serviceUrl = globalSettings.getServiceUrl();
        String checkPolicies = globalSettings.getCheckPolicies();
        String forceUpdate = globalSettings.getForceUpdate();
        String failOnError = globalSettings.getFailOnError();
//        boolean checkPolicies = globalSettings.isCheckPolicies();
        ProxySettings proxy = globalSettings.getProxy();
        int connectionTimeoutMinutes = globalSettings.getConnectionTimeoutMinutes();
        int connectionRetries = globalSettings.getConnectionRetries();
        int connectionRetriesInterval = globalSettings.getConnectionRetriesInterval();

        for (SRunnerContext runnerContext : context.getRunnerContexts()) {
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_ORGANIZATION_TOKEN, orgToken);
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_USER_KEY, userKey);
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_SERVICE_URL, serviceUrl);
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_CHECK_POLICIES, checkPolicies);
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_FORCE_UPDATE, forceUpdate);
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_FAIL_ON_ERROR, failOnError);
//            safeAddRunnerParameter(runnerContext, Constants.RUNNER_CHECK_POLICIES, Boolean.toString(checkPolicies));
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_CONNECTION_TIMEOUT_MINUTES, Integer.valueOf(connectionTimeoutMinutes).toString());
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_CONNECTION_RETRIES, Integer.valueOf(connectionRetries).toString());
            safeAddRunnerParameter(runnerContext, Constants.RUNNER_CONNECTION_RETRIES_INTERVAL, Integer.valueOf(connectionRetriesInterval).toString());

            if (proxy != null) {
                safeAddRunnerParameter(runnerContext, Constants.RUNNER_PROXY_HOST, proxy.getHost());
                safeAddRunnerParameter(runnerContext, Constants.RUNNER_PROXY_PORT, Integer.valueOf(proxy.getPort()).toString());
                safeAddRunnerParameter(runnerContext, Constants.RUNNER_PROXY_USERNAME, proxy.getUsername());
                safeAddRunnerParameter(runnerContext, Constants.RUNNER_PROXY_PASSWORD, proxy.getPassword());
            }
        }
    }

    /* --- Private methods --- */

    private void safeAddRunnerParameter(SRunnerContext runnerContext, String key, String value) {
        if (value != null) {
            runnerContext.addRunnerParameter(key, value);
        }
    }

}
