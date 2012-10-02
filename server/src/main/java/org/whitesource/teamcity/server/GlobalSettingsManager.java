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

import com.thoughtworks.xstream.XStream;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.whitesource.teamcity.common.WssUtils;

import java.io.*;

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
        xStream.processAnnotations(GlobalSettings.class);
        xStream.setClassLoader(GlobalSettings.class.getClassLoader());

        configFile = new File(serverPaths.getConfigDir(), CONFIG_FILE_NAME);
        loadConfig();
    }

    /* --- Public methods --- */

    public void save() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(configFile);
            xStream.toXML(globalSettings, fos);
        } catch (FileNotFoundException e) {
            Loggers.SERVER.error(WssUtils.logMsg(LOG_COMPONENT, "Failed to save config file " + configFile), e);
        } finally {
            FileUtil.close(fos);
        }
    }

    public boolean isProxy() {
        return globalSettings != null &&
                globalSettings.getProxy() != null &&
                !StringUtil.isEmptyOrSpaces(globalSettings.getProxy().getHost());
    }

    public String getHexEncodedPublicKey() {
        return RSACipher.getHexEncodedPublicKey();
    }

    /* --- Private methods --- */

    private void loadConfig() {
        if (configFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(configFile);
                globalSettings = (GlobalSettings) xStream.fromXML(fis);
            } catch (IOException e) {
                Loggers.SERVER.error(WssUtils.logMsg(LOG_COMPONENT, "Failed to load config file " + configFile), e);
            } finally {
                FileUtil.close(fis);
            }
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
