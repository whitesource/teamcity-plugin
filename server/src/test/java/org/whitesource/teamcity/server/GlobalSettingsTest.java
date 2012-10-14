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
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Edo.Shor
 */
public class GlobalSettingsTest {

    @Test
    public void testSettingsPersistence() {
         GlobalSettings settings = new GlobalSettings();
        settings.setOrgToken("orgToken");

        GlobalSettings tmpSettings = saveAndLoad(settings);
        assertNotNull(tmpSettings);
        assertEquals(settings.getOrgToken(), tmpSettings.getOrgToken());

        ProxySettings proxy = new ProxySettings();
        proxy.setHost("host");
        proxy.setPort(4444);
        proxy.setUsername("username");
        proxy.setPassword("password");
        settings.setProxy(proxy);

        tmpSettings = saveAndLoad(settings);
        assertNotNull(tmpSettings);
        assertEquals(settings.getOrgToken(), tmpSettings.getOrgToken());
        assertEquals(settings.getProxy().getHost(), tmpSettings.getProxy().getHost());
        assertEquals(settings.getProxy().getPort(), tmpSettings.getProxy().getPort());
        assertEquals(settings.getProxy().getUsername(), tmpSettings.getProxy().getUsername());
        assertEquals(settings.getProxy().getPassword(), tmpSettings.getProxy().getPassword());
    }

    private GlobalSettings saveAndLoad(GlobalSettings settings) {
        XStream xStream = new XStream();
        StringWriter stringWriter = new StringWriter();
        xStream.toXML(settings, stringWriter);
        return (GlobalSettings) xStream.fromXML(new StringReader(stringWriter.toString()));
    }
}
