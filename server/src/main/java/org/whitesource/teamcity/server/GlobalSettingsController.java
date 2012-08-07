package org.whitesource.teamcity.server;

import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Edo.Shor
 *
 * TODO: no need for FormXMLController
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

        final GlobalSettings settings = new GlobalSettings();

        settings.setOrgToken(request.getParameter("orgToken"));

        ProxySettings proxy = new ProxySettings();
        proxy.setHost(request.getParameter("proxyHost"));
        proxy.setUsername(request.getParameter("proxyPort"));
        proxy.setPassword(request.getParameter("proxyUsername"));
        String proxyPort = request.getParameter("proxyPassword");
        if (StringUtil.isNumber(proxyPort)) {
            proxy.setPort(Integer.parseInt(proxyPort));
        }
        settings.setProxy(proxy);

        settingsManager.setGlobalSettings(settings);
        settingsManager.save();

        getOrCreateMessages(request).addMessage("settingsSaved", "White Source global settings were saved.");
    }
}
