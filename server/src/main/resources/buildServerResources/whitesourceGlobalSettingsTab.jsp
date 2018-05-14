<%--

    Copyright (C) 2012 White Source Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="settingsManager" type="org.whitesource.teamcity.server.GlobalSettingsManager" scope="request"/>

<c:url var="controllerUrl" value="/admin/whitesource/globalSettingsTab.html"/>
<c:url var="logoUrl" value="${teamcityPluginResourcesPath}img/whitesource-logo.png"/>

<script type="text/javascript">

    var SettingsForm = OO.extend(BS.AbstractPasswordForm, {
        formElement: function () {
            return $("globalSettingsForm")
        },

        save: function () {
            if (!$('useProxyCheckbox').checked) {
                $('proxyHost').value = '';
            }

            BS.PasswordFormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener, {
                onInvalidOrgTokenError: function (elem) {
                    $("invalidOrgToken").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("orgToken"));
                },

                onInvalidServiceUrlError: function (elem) {
                    $("invalidServiceUrl").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("serviceUrl"));
                },

                onInvalidConnectionTimeoutMinutesError: function (elem) {
                    $("invalidConnectionTimeoutMinutes").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("connectionTimeoutMinutes"));
                },

                onInvalidProxyPortError: function (elem) {
                    $("invalidProxyPort").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("proxyPort"));
                },

                onSuccessfulSave: function () {
                    SettingsForm.enable();
                },

                onCompleteSave: function (form, responseXml, wereErrors) {
                    BS.ErrorsAwareListener.onCompleteSave(form, responseXml, wereErrors);
                    if (!wereErrors) {
                        $('generalSettings').refresh();
                    }
                }

            }));

            return false;
        }
    });

</script>

<div>
    <bs:refreshable containerId="generalSettings" pageUrl="${pageUrl}">
        <table border="0">
            <tr>
                <td>
                    <a href="http://www.whitesourcesoftware.com/" target="_blank">
                        <img src="${logoUrl}" alt="White Source" width="200px"/>
                    </a>
                </td>
                <td style="vertical-align: middle;">
                    <h1>Global Settings</h1>
                </td>
            </tr>
        </table>

        <bs:messages key="settingsSaved"/>

        <form id="globalSettingsForm" action="${controllerUrl}" method="post" onsubmit="{return SettingsForm.save()}">
            <table class="runnerFormTable">
                <tr class="groupingTitle">
                    <td colspan="2">Account settings</td>
                </tr>
                <tr>
                    <th>
                        <label for="orgToken">
                            Organization Token
                            <l:star/>
                            <bs:helpIcon
                                    iconTitle="Tokens can be found in the administration section of your account on White Source."/>
                        </label>
                    </th>
                    <td>
                        <forms:textField name="orgToken" value="${settingsManager.globalSettings.orgToken}"
                                         style="width:300px;"/>
                        <span class="error" id="invalidOrgToken"></span>
                        <div class="smallNote" style="margin-left: 0;">Unique identifier of the organization to
                            update.
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="userKey">
                            User key
                            <bs:helpIcon
                                    iconTitle="User Keys can be found under your profile section of your account on White Source."/>
                        </label>
                    </th>
                    <td>
                        <forms:textField name="userKey" value="${settingsManager.globalSettings.userKey}"
                                         style="width:300px;"/>
                        <span class="error" id="invalidUserKey"></span>
                        <div class="smallNote" style="margin-left: 0;">Unique identifier of user, can be generate from the profile page in your whitesource account.
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="checkPolicies">
                            Check Policy Compliance
                            <bs:helpIcon iconTitle="Require premium account.<br/>
                            <b>Check only new libraries</b> - Check that the newly introduced open source libraries conform with organization policies.<br/>
                            <b>Force check all libraries</b> - Check that all introduced open source libraries conform with organization policies.<br/>
                            <b>Disable</b> - Disable policies check when updating WhiteSource."/>
                        </label>
                    </th>
                    <td>
                        <input id="checkPolicies" name="checkPolicies" type="radio" value="enableNew"
                            ${settingsManager.globalSettings.checkPolicies=='enableNew'?'checked':''}/> Check only new
                        libraries
                        <input id="checkPolicies" name="checkPolicies" type="radio" value="enableAll"
                            ${settingsManager.globalSettings.checkPolicies=='enableAll'?'checked':''}/> Force check all
                        libraries
                        <input id="checkPolicies" name="checkPolicies" type="radio" value="disable"
                            ${settingsManager.globalSettings.checkPolicies=='disable'?'checked':''}/> Disable
                            <%--<forms:radiobutton name="checkPolicies" value="${settingsManager.globalSettings.checkPolicies}"/>--%>
                        <div class="smallNote" style="margin-left: 0;">
                            Fail the build if an open source library is rejected by an organization policy.
                            <br/>
                            <b>Note:</b> In such cases, no update will take place on White Source.
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="forceUpdate">
                            Force update
                            <bs:helpIcon iconTitle="Updates organization inventory regardless of policy violations."/>
                        </label>
                    </th>
                    <td>
                        <input id="forceUpdate" name="forceUpdate" type="checkbox" value="forceUpdate"
                            ${settingsManager.globalSettings.forceUpdate =='forceUpdate'?'checked':''}/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="failOnError">
                            Fail on error
                            <bs:helpIcon iconTitle="Indicates whether to fail the build on a general error (e.g. network error)."/>
                        </label>
                    </th>
                    <td>
                        <input id="failOnError" name="failOnError" type="checkbox" value="failOnError"
                            ${settingsManager.globalSettings.failOnError =='failOnError'?'checked':''}/>
                    </td>
                </tr>
                <tr class="groupingTitle">
                    <td colspan="2">API Endpoint</td>
                </tr>
                <tr>
                    <th>
                        <label for="serviceUrl">
                            Service Url
                        </label>
                    </th>
                    <td>
                        <forms:textField name="serviceUrl" value="${settingsManager.globalSettings.serviceUrl}"
                                         style="width:300px;" size="255"/>
                        <span class="error" id="invalidServiceUrl"></span>
                        <div class="smallNote" style="margin-left: 0;">
                            Optional. Url to on premise installation of White Source.
                            SaaS accounts should leave this field blank.
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="connectionTimeoutMinutes">
                            Connection Timeout
                        </label>
                    </th>
                    <td>
                        <c:set var="connectionTimeout" value=""/>
                        <c:if test="${settingsManager.globalSettings.connectionTimeoutMinutes > 0}">
                            <c:set var="connectionTimeout"
                                   value="${settingsManager.globalSettings.connectionTimeoutMinutes}"/>
                        </c:if>
                        <forms:textField name="connectionTimeoutMinutes" value="${connectionTimeout}"
                                         style="width:300px;" size="255"/>
                        <div class="smallNote" style="margin-left: 0;">
                            <span class="error" id="invalidConnectionTimeoutMinutes"></span>
                            Optional. Default value is 60 min. Connection timeout is measured in minutes.
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="useProxyCheckbox">
                            Proxy Server
                        </label>
                    </th>
                    <td>
                        <forms:checkbox name="useProxyCheckbox" checked="${settingsManager.proxy}"
                                        onclick="$('proxySettings').toggle()"/>
                        <div class="smallNote" style="margin-left: 0;">
                            Optional. Settings for proxy server if required for communication with White Source.
                        </div>
                        <div id="proxySettings" style="display: ${settingsManager.proxy ? 'block' : 'none'};">
                            <table>
                                <tr>
                                    <th><label for="proxyHost">Host</label></th>
                                    <td><forms:textField name="proxyHost"
                                                         value="${settingsManager.globalSettings.proxy.host}"/></td>
                                </tr>
                                <tr>
                                    <th><label for="proxyPort">Port</label></th>
                                    <td>
                                        <c:set var="port" value=""/>
                                        <c:if test="${settingsManager.globalSettings.proxy.port != -1}">
                                            <c:set var="port" value="${settingsManager.globalSettings.proxy.port}"/>
                                        </c:if>
                                        <forms:textField name="proxyPort" value="${port}"/>
                                        <span class="error" id="invalidProxyPort"></span>
                                    </td>
                                </tr>
                                <tr>
                                    <th><label for="proxyUsername">Username</label></th>
                                    <td><forms:textField name="proxyUsername"
                                                         value="${settingsManager.globalSettings.proxy.username}"/></td>
                                </tr>
                                <tr>
                                    <th><label for="proxyPassword">Password</label></th>
                                    <td><forms:passwordField name="proxyPassword"/></td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>

            <div class="saveButtonsBlock">
                <input class="submitButton" type="submit" value="Save">
                <input type="hidden" id="publicKey" name="publicKey"
                       value="<c:out value='${settingsManager.hexEncodedPublicKey}'/>"/>
                <forms:saving/>
            </div>
        </form>
    </bs:refreshable>
</div>