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

<jsp:useBean id="settingsManager" type="org.whitesource.teamcity.server.GlobalSettingsManager" scope="request"/>

<c:url var="controllerUrl" value="/admin/whitesource/globalSettingsTab.html"/>
<c:url var="logoUrl" value="${teamcityPluginResourcesPath}img/whitesource-logo.png"/>

<script type="text/javascript">

    var SettingsForm = OO.extend(BS.AbstractPasswordForm, {
        formElement : function() {
            return $("globalSettingsForm")
        },

        save: function() {
            if (!$('useProxyCheckbox').checked) {
                $('proxyHost').value = '';
            }

            BS.PasswordFormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener, {
                onInvalidOrgTokenError: function(elem) {
                    $("invalidOrgToken").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("orgToken"));
                },

                onInvalidProxyHostError: function(elem) {
                    $("invalidProxyHost").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("proxyHost"));
                },

                onInvalidProxyPortError: function(elem) {
                    $("invalidProxyPort").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("proxyPort"));
                },

                onInvalidProxyPasswordError: function(elem) {
                    $("invalidProxyPassword").innerHTML = elem.firstChild.nodeValue;
                    SettingsForm.highlightErrorField($("proxyPassword"));
                },

                onSuccessfulSave: function() {
                    SettingsForm.enable();
                },

                onCompleteSave : function(form, responseXml, wereErrors) {
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
                            Organization token
                            <l:star/>
                            <bs:helpIcon iconTitle="Tokens can be found in the administration section of your account on White Source."/>
                        </label>
                    </th>
                    <td>
                        <forms:textField name="orgToken" value="${settingsManager.globalSettings.orgToken}" size="60"/>
                        <span class="error" id="invalidOrgToken"></span>
                        <div class="smallNote" style="margin-left: 0;">Unique identifier of the organization to update.</div>
                    </td>
                </tr>
                <tr class="groupingTitle">
                    <td colspan="2">Check policies</td>
                </tr>
                <tr>
                    <th>
                        <label for="checkPolicies">
                            Check policies
                            <bs:helpIcon iconTitle="Require premium account."/>
                        </label>
                    </th>
                    <td>
                        <forms:checkbox name="checkPolicies" checked="${settingsManager.globalSettings.checkPolicies}"/>
                        <div class="smallNote" style="margin-left: 0;">
                            Fail the build if an open source library is rejected by an organization policy.
                            <br/>
                            <b>Note:</b> In such cases, no update will take place on White Source.
                        </div>
                    </td>
                </tr>
            </table>
            <br/>
            <div id="useProxy">
                <table border="0">
                    <tr>
                        <td><forms:checkbox name="useProxyCheckbox" checked="${settingsManager.proxy}" onclick="$('proxySettings').toggle()" /></td>
                        <td>
                            Use a proxy server
                            <bs:helpIcon iconTitle="Settings for Proxy server when needed to access white source serive."/>
                        </td>
                    </tr>
                </table>
            </div>
            <br/>
            <div id="proxySettings" style="display: ${settingsManager.proxy ? 'block' : 'none'};">
                <table class="runnerFormTable">
                    <tr class="groupingTitle">
                        <td colspan="2">
                            Proxy configuration
                        </td>
                    </tr>
                    <tr>
                        <th> <label for="proxyHost">Host</label> </th>
                        <td>
                            <forms:textField name="proxyHost" value="${settingsManager.globalSettings.proxy.host}"/>
                            <span class="error" id="invalidProxyHost"></span>
                        </td>
                    </tr>
                    <tr>
                        <th> <label for="proxyPort">Port</label> </th>
                        <td>
                            <c:set var="port" value=""/>
                            <c:if test="${settingsManager.globalSettings.proxy.port != -1}">
                                <c:set var="port" value="${settingsManager.globalSettings.proxy.port}"/>
                            </c:if>
                            <forms:textField name="proxyPort" value="${port}" />
                            <span class="error" id="invalidProxyPort"></span>
                        </td>
                    </tr>
                    <tr>
                        <th> <label for="proxyUsername">Username</label> </th>
                        <td>
                            <forms:textField name="proxyUsername" value="${settingsManager.globalSettings.proxy.username}"/>
                        </td>
                    </tr>
                    <tr>
                        <th> <label for="proxyPassword">Password</label> </th>
                        <td>
                            <forms:passwordField name="proxyPassword"/>
                            <span class="error" id="invalidProxyPassword"></span>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="saveButtonsBlock">
                <input class="submitButton" type="submit" value="Save">
                <input type="hidden" id="publicKey" name="publicKey" value="<c:out value='${settingsManager.hexEncodedPublicKey}'/>"/>
                <forms:saving/>
            </div>
        </form>
    </bs:refreshable>
</div>