package org.whitesource.teamcity.server;

import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.serverSide.SRunnerContext;
import jetbrains.buildServer.util.StringUtil;
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
        if (StringUtil.isEmptyOrSpaces(orgToken)) {
//            TODO: implement
        }

        ProxySettings proxy = globalSettings.getProxy();

        for (SRunnerContext runnerContext : context.getRunnerContexts()) {
            // activate plugin
            runnerContext.addRunnerParameter(Constants.UPDATE_WHITESOURCE, Boolean.TRUE.toString());

            runnerContext.addRunnerParameter(Constants.ORGANIZATION_TOKEN, orgToken);

            if (proxy != null) {
                runnerContext.addRunnerParameter(Constants.PROXY_HOST, proxy.getHost());
                runnerContext.addRunnerParameter(Constants.PROXY_PORT, Integer.valueOf(proxy.getPort()).toString());
                runnerContext.addRunnerParameter(Constants.PROXY_USERNAME, proxy.getUsername());
                runnerContext.addRunnerParameter(Constants.PROXY_PASSWORD, proxy.getPassword());
            }

        }

    }

}
