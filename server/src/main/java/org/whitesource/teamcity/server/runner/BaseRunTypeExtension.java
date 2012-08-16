package org.whitesource.teamcity.server.runner;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunTypeExtension;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.server.GlobalSettingsManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Base, abstract, implementation to hold common functionality to all job type specific extensions.
 *
 * @author Edo.Shor
 */
public abstract class BaseRunTypeExtension extends RunTypeExtension {

    /* --- Members --- */

    private String viewUrl;
    private String editUrl;

    protected final PluginDescriptor pluginDescriptor;
    protected final WebControllerManager webControllerManager;
    protected final GlobalSettingsManager settingsManager;
    protected final Map<String, String> defaultRunnerParams;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param webControllerManager
     * @param pluginDescriptor
     */
    public BaseRunTypeExtension(@NotNull final WebControllerManager webControllerManager,
                                @NotNull final PluginDescriptor pluginDescriptor,
                                @NotNull final GlobalSettingsManager settingsManager) {
        this.webControllerManager = webControllerManager;
        this.pluginDescriptor = pluginDescriptor;
        this.settingsManager = settingsManager;

        defaultRunnerParams = new HashMap<String, String>();
        defaultRunnerParams.put(Constants.RUNNER_IGNORE_POM_MODULES, "true");
    }

    /* --- Concrete implementation methods --- */

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return editUrl;
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return viewUrl;
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return defaultRunnerParams;
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        //TODO: implement once we want validations
        return null;
    }

    /* --- Protected methods --- */

    protected void registerView(@NotNull final String url, @NotNull final String jsp) {
        viewUrl = pluginDescriptor.getPluginResourcesPath(url);
        webControllerManager.registerController(viewUrl, new ViewController(pluginDescriptor.getPluginResourcesPath(jsp)));
    }

    protected void registerEdit(@NotNull final String url, @NotNull final String jsp) {
        editUrl = pluginDescriptor.getPluginResourcesPath(url);
        webControllerManager.registerController(editUrl, new ViewController(pluginDescriptor.getPluginResourcesPath(jsp)));
    }

    /* --- Nested classes --- */

    static class ViewController extends BaseController {

        private String actualJsp;

        ViewController(@NotNull final String actualJsp) {
            this.actualJsp = actualJsp;
        }

        @Override
        protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView(actualJsp);
        }
    }

}
