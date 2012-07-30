package org.whitesource.teamcity.server;

import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.whitesource.teamcity.common.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Edo.Shor
 */
public class GlobalSettingsTab extends SimpleCustomTab {

    /* --- Members --- */

    private GlobalSettingsManager settingsManager;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param webControllerManager
     * @param settingsManager
     */
    public GlobalSettingsTab(@NotNull WebControllerManager webControllerManager, @NotNull GlobalSettingsManager settingsManager) {
        super(webControllerManager,
                PlaceId.ADMIN_SERVER_CONFIGURATION_TAB,
                Constants.PLUGIN_NAME,
                "whitesourceGlobalSettingsTab.jsp",
                "White Source");

        this.settingsManager = settingsManager;

        setPosition(PositionConstraint.after("serverConfigGeneral"));
        register();

        webControllerManager.registerController("/admin/whitesource/globalSettingsTab.html", new GlobalSettingsController(settingsManager));
    }

    /* --- Overridden methods --- */

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        super.fillModel(model, request);
        model.put("settingsManager", settingsManager);
    }
}
