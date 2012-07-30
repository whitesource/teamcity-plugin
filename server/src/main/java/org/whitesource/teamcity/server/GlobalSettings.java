package org.whitesource.teamcity.server;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Edo.Shor
 */
@XStreamAlias("GlobalSettings")
public class GlobalSettings {

    /* --- Members --- */

    private String orgToken;

    @XStreamAlias("Proxy")
    private ProxySettings proxy;

    /* --- Getters / Setters --- */

    public String getOrgToken() {
        return orgToken;
    }

    public void setOrgToken(String orgToken) {
        this.orgToken = orgToken;
    }

    public ProxySettings getProxy() {
        return proxy;
    }

    public void setProxy(ProxySettings proxy) {
        this.proxy = proxy;
    }
}
