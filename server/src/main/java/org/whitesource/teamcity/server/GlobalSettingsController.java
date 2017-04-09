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

import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;
import org.whitesource.teamcity.common.WssUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Controller for the global settings tab.
 *
 * @author Edo.Shor
 */
public class GlobalSettingsController extends BaseFormXmlController {

    /* --- Members --- */

    private GlobalSettingsManager settingsManager;

    /* --- Constructors --- */

    /**
     * Constructor
     */
    public GlobalSettingsController(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    /* --- Overridden methods --- */

    @Override
    protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {
        Loggers.SERVER.info(WssUtils.logMsg("GlobalSettingsController", "doPost"));

        ActionErrors errors = validate(request);
        if (errors.hasErrors()) {
            Loggers.SERVER.warn(WssUtils.logMsg("GlobalSettingsController", errors.getErrors().size() + " Errors:" ));
            for(ActionErrors.Error error : errors.getErrors()) {
                Loggers.SERVER.warn(WssUtils.logMsg("GlobalSettingsController", error.getMessage()));
            }
            errors.serialize(xmlResponse);
            return;
        }

        final GlobalSettings settings = new GlobalSettings();
        settings.setOrgToken(request.getParameter("orgToken"));
        settings.setCheckPolicies(request.getParameter("checkPolicies"));
        settings.setForceUpdate(request.getParameter("forceUpdate"));
        settings.setServiceUrl(request.getParameter("serviceUrl"));

        String connectionTimeoutMinutes = request.getParameter("connectionTimeoutMinutes");
        if (StringUtil.isNumber(connectionTimeoutMinutes)) {
            settings.setConnectionTimeoutMinutes(Integer.parseInt(connectionTimeoutMinutes));
        }

        String proxyHost = request.getParameter("proxyHost");
        if (!StringUtil.isEmptyOrSpaces(proxyHost)){
            ProxySettings proxy = new ProxySettings();
            proxy.setHost(proxyHost);

            String proxyPort = request.getParameter("proxyPort");
            if (StringUtil.isNumber(proxyPort)) {
                proxy.setPort(Integer.parseInt(proxyPort));
            }

            proxy.setUsername(request.getParameter("proxyUsername"));
            String encryptedPassword = request.getParameter("encryptedProxyPassword");
            if (!StringUtil.isEmptyOrSpaces(encryptedPassword)) {
                proxy.setPassword(RSACipher.decryptWebRequestData(encryptedPassword));
            }

            settings.setProxy(proxy);
        }

        settingsManager.setGlobalSettings(settings);
        settingsManager.save();

        getOrCreateMessages(request).addMessage("settingsSaved", "Settings saved successfully.");
    }

    private ActionErrors validate(final HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        final String orgToken = request.getParameter("orgToken");
        if (StringUtil.isEmptyOrSpaces(orgToken)) {
            errors.addError("invalidOrgToken", "Organization token can not be empty.");
        }

        final String serviceUrl = request.getParameter("serviceUrl");
        if (!StringUtil.isEmptyOrSpaces(serviceUrl) && !isValidUrl(serviceUrl)) {
            errors.addError("invalidServiceUrl", "Service Url is not a valid URL");
        }

        String connectionTimeoutMinutesString = request.getParameter("connectionTimeoutMinutes");
        if (!StringUtil.isEmptyOrSpaces(connectionTimeoutMinutesString)) {
            if (StringUtil.isNumber(connectionTimeoutMinutesString)) {
                final int connectionTimeoutMinutes = Integer.parseInt(connectionTimeoutMinutesString);
                if (connectionTimeoutMinutes <= 0) {
                    errors.addError("invalidConnectionTimeoutMinutes", "Connection Timeout must be a greater than zero");
                }
            } else {
                errors.addError("invalidConnectionTimeoutMinutes", "Connection Timeout must be a valid number");
            }
        }

        String host = request.getParameter("proxyHost");
        if (!StringUtil.isEmptyOrSpaces(host)) {
            String proxyPort = request.getParameter("proxyPort");
            if (!StringUtil.isEmptyOrSpaces(proxyPort)) {
                if (StringUtil.isNumber(proxyPort)) {
                    final int port = Integer.parseInt(proxyPort);
                    if (port < 1 || port > 65535) {
                        errors.addError("invalidProxyPort", "Port must be a between 1-65535");
                    }
                } else {
                    errors.addError("invalidProxyPort", "Port must be a valid number");
                }
            }
        }

        return errors;
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
