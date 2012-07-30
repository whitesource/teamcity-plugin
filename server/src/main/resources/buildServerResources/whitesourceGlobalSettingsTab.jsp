<%@include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="settingsManager" type="org.whitesource.teamcity.server.GlobalSettingsManager"scope="request"/>

<c:url var="controllerUrl" value="/admin/whitesource/globalSettingsTab.html"/>
<c:url var="logoUrl" value="${teamcityPluginResourcesPath}img/whitesource-logo.png"/>

<div>
    <table border="0">
        <tr>
            <td>
                <a href="http://www.whitesourcesoftware.com/">
                    <img src="${logoUrl}" alt="White Source" width="200px"/>
                </a>
            </td>
            <td style="vertical-align: bottom;">
                <h1>Global Settings</h1>
            </td>
        </tr>
    </table>

    <form id="globalSettingsForm" method="post" action="${controllerUrl}">
        <div style="margin-left:100px">
            <p>
                <h3>Account settings</h3>
                <table border="0">
                    <tr>
                    <td>
                        Organization token
                        <bs:helpIcon iconTitle="Organization token you got from White Source. Tokens can be found in the administration section of your account."/>
                    </td>
                    <td>
                        <forms:textField name="orgToken" value="${settingsManager.globalSettings.orgToken}"/>
                    </td>
                    </tr>
                </table>
            </p>

            <p>
                <h3>Proxy configuration</h3>
                <table border="0">
                    <tr>
                        <td>Host</td>
                        <td>
                            <forms:textField name="proxyHost" value="${settingsManager.globalSettings.proxy.host}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Port</td>
                        <td>
                            <forms:textField name="proxyPort" value="${settingsManager.globalSettings.proxy.port}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Username</td>
                        <td>
                            <forms:textField name="proxyUsername" value="${settingsManager.globalSettings.proxy.username}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Password</td>
                        <td>
                            <forms:passwordField name="proxyPassword"/>
                        </td>
                    </tr>
                </table>
            </p>
        </div>

        <div class="saveButtonsBlock">
            <input class="submitButton" type="submit" value="Save">
            <input type="hidden" id="submitSettings" name="submitSettings" value="store"/>
            <forms:saving/>
        </div>

    </form>

    <forms:modified/>

</div>