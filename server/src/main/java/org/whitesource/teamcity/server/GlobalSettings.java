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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Edo.Shor
 */
@XStreamAlias("GlobalSettings")
public class GlobalSettings {

    /* --- Members --- */

    private String orgToken;

    private String checkPolicies;

    private String forceUpdate;

    private String serviceUrl;

    @XStreamAlias("Proxy")
    private ProxySettings proxy;

    private int connectionTimeoutMinutes;

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

    public String getCheckPolicies() {
        return checkPolicies;
    }

    public void setCheckPolicies(String checkPolicies) {
        this.checkPolicies = checkPolicies;
    }
//    public boolean isCheckPolicies() {
//        return checkPolicies;
//    }
//
//    public void setCheckPolicies(boolean checkPolicies) {
//        this.checkPolicies = checkPolicies;
//    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public ProxySettings getProxy() {
        return proxy;
    }

    public void setProxy(ProxySettings proxy) {
        this.proxy = proxy;
    }

    public int getConnectionTimeoutMinutes() {
        return connectionTimeoutMinutes;
    }

    public void setConnectionTimeoutMinutes(int connectionTimeoutMinutes) {
        this.connectionTimeoutMinutes = connectionTimeoutMinutes;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
