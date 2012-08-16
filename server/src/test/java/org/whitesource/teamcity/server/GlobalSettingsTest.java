package org.whitesource.teamcity.server;


import static org.junit.Assert.*;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

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
