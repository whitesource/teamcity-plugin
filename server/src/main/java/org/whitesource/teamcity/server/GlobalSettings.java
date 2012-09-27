package org.whitesource.teamcity.server;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Edo.Shor
 */
@XStreamAlias("GlobalSettings")
public class GlobalSettings {

    /* --- Members --- */

    private String orgToken;

    private boolean checkPolicies;

    @XStreamAlias("Proxy")
    private ProxySettings proxy;

    /* --- Constructors--- */

    /**
     * Default constructor
     */
    public GlobalSettings() {
    }

    /* --- Getters / Setters --- */

    public String getOrgToken() {
        return orgToken;
    }

    public void setOrgToken(String orgToken) {
        this.orgToken = orgToken;
    }

    public boolean isCheckPolicies() {
        return checkPolicies;
    }

    public void setCheckPolicies(boolean checkPolicies) {
        this.checkPolicies = checkPolicies;
    }

    public ProxySettings getProxy() {
        return proxy;
    }

    public void setProxy(ProxySettings proxy) {
        this.proxy = proxy;
    }
}
