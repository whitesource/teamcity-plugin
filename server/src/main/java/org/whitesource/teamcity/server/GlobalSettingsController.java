package org.whitesource.teamcity.server;

import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;
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

        if (settingsManager.getGlobalSettings() == null) {
            settingsManager.setGlobalSettings(new GlobalSettings());
        }
        settingsManager.getGlobalSettings().setOrgToken(request.getParameter("orgToken"));

        String proxyHost = request.getParameter("proxyHost");
        if (!StringUtil.isEmptyOrSpaces(proxyHost)) {
            if (settingsManager.getGlobalSettings().getProxy() == null) {
                settingsManager.getGlobalSettings().setProxy(new ProxySettings());
            }
            settingsManager.getGlobalSettings().getProxy().setHost(proxyHost);
            settingsManager.getGlobalSettings().getProxy().setUsername(request.getParameter("proxyUsername"));
            settingsManager.getGlobalSettings().getProxy().setPassword(request.getParameter("proxyPassword"));
            String proxyPort = request.getParameter("proxyPort");
            if (StringUtil.isNumber(proxyPort)) {
                settingsManager.getGlobalSettings().getProxy().setPort(Integer.parseInt(proxyPort));
            }
        }

        settingsManager.save();
        getOrCreateMessages(request).addMessage("settingsSaved", "White Source global settings were saved.");
    }
}
