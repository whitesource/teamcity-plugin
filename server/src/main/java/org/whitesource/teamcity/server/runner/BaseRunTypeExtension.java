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
package org.whitesource.teamcity.server.runner;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunTypeExtension;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.whitesource.teamcity.common.Constants;

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
    protected final Map<String, String> defaultRunnerParams;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param webControllerManager
     * @param pluginDescriptor
     */
    public BaseRunTypeExtension(@NotNull final WebControllerManager webControllerManager,
                                @NotNull final PluginDescriptor pluginDescriptor) {
        this.webControllerManager = webControllerManager;
        this.pluginDescriptor = pluginDescriptor;

        defaultRunnerParams = new HashMap<String, String>();
        defaultRunnerParams.put(Constants.RUNNER_IGNORE_POM_MODULES, "true");
        defaultRunnerParams.put(Constants.RUNNER_OVERRIDE_CHECK_POLICIES, "global");
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
