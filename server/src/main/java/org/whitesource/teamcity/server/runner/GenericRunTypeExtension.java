package org.whitesource.teamcity.server.runner;

import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.server.GlobalSettingsManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Concrete implementation for job types holding no OSS information..
 *
 * @author Edo.Shor
 */
public class GenericRunTypeExtension extends BaseRunTypeExtension {

    /* --- Static members --- */

    private static final Collection<String> supportedRunType = Arrays.asList(
            Constants.RUNNER_ANT,
            Constants.RUNNER_CMD,
            Constants.RUNNER_MSBUILD,
            Constants.RUNNER_POWERSHELL);

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param pluginDescriptor
     * @param webControllerManager
     * @param settingsManager
     */
    public GenericRunTypeExtension(@NotNull final PluginDescriptor pluginDescriptor,
                                 @NotNull final WebControllerManager webControllerManager,
                                 @NotNull final GlobalSettingsManager settingsManager) {
        super(webControllerManager, pluginDescriptor, settingsManager);

        registerView("viewGenericRunner.html", "runner/viewGenericRunner.jsp");
        registerEdit("editGenericRunner.html", "runner/editGenericRunner.jsp");
    }

    /* --- Concrete implementation methods --- */

    @Override
    public Collection<String> getRunTypes() {
        return supportedRunType;
    }

}