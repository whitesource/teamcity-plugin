package org.whitesource.teamcity.server.runner;

import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.server.GlobalSettingsManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Concrete implementation for maven jobs.
 *
 * @author Edo.Shor
 */
public class MavenRunTypeExtension extends BaseRunTypeExtension {

    /* --- Static members --- */

    private static final Collection<String> supportedRunType = Arrays.asList(Constants.RUNNER_MAVEN);

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param pluginDescriptor
     * @param webControllerManager
     * @param settingsManager
     */
    public MavenRunTypeExtension(@NotNull final PluginDescriptor pluginDescriptor,
                                 @NotNull final WebControllerManager webControllerManager,
                                 @NotNull final GlobalSettingsManager settingsManager) {
        super(webControllerManager, pluginDescriptor, settingsManager);

        registerView("viewMavenRunner.html", "runner/viewMavenRunner.jsp");
        registerEdit("editMavenRunner.html", "runner/editMavenRunner.jsp");
    }

    /* --- Concrete implementation methods --- */

    @Override
    public Collection<String> getRunTypes() {
        return supportedRunType;
    }

}