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
<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<script type="text/javascript">

    function toggleSettings() {
        $('containerProduct').toggle();
        $('containerProductVersion').toggle();
        $('containerOverrideOrgToken').toggle();
        $('containerOverrideCheckPolicies').toggle();
        $('containerProjectToken').toggle();
        $('containerModuleTokens').toggle();
        $('containerIncludes').toggle();
        $('containerExcludes').toggle();
        $('containerIgnorePomModules').toggle();
    }

</script>

<l:settingsGroup title="Update White Source">
    <tr>
        <th>
            Enable:
        </th>
        <td>
            <props:checkboxProperty name="org.whitesource.doUpdate" onclick="toggleSettings()"/>
            <span class="smallNote">
                Send open source usage information to White Source.
            </span>
        </td>
    </tr>

    <c:set var="showSettings" value="${not empty propertiesBean.properties['org.whitesource.doUpdate']}"/>

    <tr id="containerProduct" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.product">
                Product name or token:
                <bs:helpIcon iconTitle="Tokens can be found in the administration section of your account on White Source."/>
            </label>
        </th>
        <td>
            <props:textProperty name="org.whitesource.product" size="55"/>
            <span class="smallNote">
                Optional. Name or unique identifier of the product to update.
            </span>
        </td>
    </tr>
    <tr id="containerProductVersion" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.productVersion">
                Product version:
            </label>
        </th>
        <td>
            <props:textProperty name="org.whitesource.productVersion" size="55"/>
            <span class="smallNote">
                Optional. Version to use for all projects in this product.
            </span>
        </td>
    </tr>
    <tr id="containerOverrideOrgToken" style="${showSettings ? '' : 'display: none;'}">
        <th>
            Override organization token:
            <bs:helpIcon iconTitle="Tokens can be found in the administration section of your account on White Source."/>
        </th>
        <td>
            <props:textProperty name="org.whitesource.overrideOrgToken" size="55"/>
            <span class="smallNote">
                Optional. Organization token to use instead of global settings.
            </span>
        </td>
    </tr>
    <tr id="containerOverrideCheckPolicies" style="${showSettings ? '' : 'display: none;'}">
        <th>
            Override policies check:
        </th>
        <td>
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="global"/> Use Global Settings
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="enable"/> Enable
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="disable"/> Disable
            <span class="smallNote">
                Optional. Override check policies in global settings.
            </span>
        </td>
    </tr>
    <tr id="containerProjectToken" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.projectToken">
                Project token:
                <bs:helpIcon iconTitle="Tokens can be found in the administration section of your account on White Source."/>
            </label>
        </th>
        <td>
            <props:textProperty name="org.whitesource.projectToken" size="55"/>
            <span class="smallNote">
                Optional. Unique identifier of the project to update. Leave blank to use default naming convention.
            </span>
        </td>
    </tr>
    <tr id="containerModuleTokens" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.moduleTokens">
                Module tokens:
                <bs:helpIcon iconTitle="Tokens can be found in the administration section of your account on White Source."/>
            </label>
        </th>
        <td>
            <props:multilineProperty name="org.whitesource.moduleTokens" linkTitle="Module tokens" rows="3" cols="60"/>
            <span class="smallNote">
                Comma or line separated map of a maven module artifactId and its White Source unique identifying token.
                <br/>
                Format: artifactId=token,...
            </span>
        </td>
    </tr>
    <tr id="containerIncludes" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.includes">
                Modules to include:
            </label>
        </th>
        <td>
            <props:multilineProperty name="org.whitesource.includes" linkTitle="Modules to include" rows="3" cols="60"/>
            <span class="smallNote">
                Comma or line separated list of maven modules artifactId patterns to include in update.
                <br/>You can use * to match a set of modules.
                <br/>Leave empty to include all modules.
            </span>
        </td>
    </tr>
    <tr id="containerExcludes" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.excludes">
                Modules to exclude:
            </label>
        </th>
        <td>
            <props:multilineProperty name="org.whitesource.excludes" linkTitle="Modules to exclude" rows="3" cols="60"/>
            <span class="smallNote">
                Comma or line separated list of maven modules artifactId patterns to exclude from update.
                <br/>You can use * to match a set of modules.
                <br/>For example, type "test-*" to exclude all artifactIds prefixed with "test-".
            </span>
        </td>
    </tr>
    <tr id="containerIgnorePomModules" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.ignorePomModules">
                Ignore pom modules:
            </label>
        </th>
        <td>
            <props:checkboxProperty name="org.whitesource.ignorePomModules"/>
            <span class="smallNote">
                Check to ignore maven modules of type pom, i.e. parent modules.
            </span>
        </td>
    </tr>

</l:settingsGroup>