package org.whitesource.teamcity.server;

import com.thoughtworks.xstream.XStream;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.whitesource.teamcity.common.WssUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Global configuration for the plugin.
 *
 * @author Edo.Shor
 */
public class GlobalSettingsManager {

    /* --- Static members --- */

    private static final String LOG_COMPONENT = "GlobalConfig";

    private final static String CONFIG_FILE_NAME = "whitesource-config.xml";

    /* --- Members --- */

    private File configFile;

    private XStream xStream;

    private GlobalSettings globalSettings;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param serverPaths
     */
    public GlobalSettingsManager(@NotNull ServerPaths serverPaths) {
        xStream = new XStream();

        configFile = new File(serverPaths.getConfigDir(), CONFIG_FILE_NAME);
        loadConfig();
    }

    /* --- Private methods --- */

    public void save() {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(configFile);
            xStream.toXML(globalSettings, outputStream);
        } catch (FileNotFoundException e) {
            Loggers.SERVER.error(WssUtils.logMsg(LOG_COMPONENT,"Failed to save config file " + configFile), e);
        } finally {
            FileUtil.close(outputStream);
        }
    }

    /* --- Private methods --- */

    private void loadConfig() {
        if (configFile.exists()) {
            globalSettings = (GlobalSettings) xStream.fromXML(configFile);
        }
    }

    /* --- Getters / Setters --- */

    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }

    public void setGlobalSettings(GlobalSettings globalSettings) {
        this.globalSettings = globalSettings;
    }
}
