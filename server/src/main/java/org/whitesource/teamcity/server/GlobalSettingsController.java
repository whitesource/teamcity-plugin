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
     *
     * @param settingsManager
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
        settings.setCheckPolicies(Boolean.parseBoolean(request.getParameter("checkPolicies")));

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

        String host = request.getParameter("proxyHost");
        if (!StringUtil.isEmptyOrSpaces(host)) {
            try {
                new URL(host);
            } catch (MalformedURLException e) {
                errors.addError("invalidProxyHost", "Host must be a valid url.");
            }

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

            String username = request.getParameter("proxyUsername");
            if (!StringUtil.isEmptyOrSpaces(username) &&
                    StringUtil.isEmptyOrSpaces(request.getParameter("encryptedProxyPassword"))) {
                errors.addError("invalidProxyPassword", "Password must be set if a username is specified.");
            }
        }

        return errors;
    }
}
