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
        $('containerIncludes').toggle();
        $('containerExcludes').toggle();
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
            Check policy compliance:
            <bs:helpIcon iconTitle="<b>Use global settings</b> - Override this property from the global configuration.<br/>
                            <b>Check only new libraries</b> - Check that the newly introduced open source libraries conform with organization policies.<br/>
                            <b>Force check all libraries</b> - Check that all introduced open source libraries conform with organization policies.<br/>
                            <b>Disable</b> - Disable policies check when updating WhiteSource."/>
        </th>
        <td>
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="global"/> Use Global Settings
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="enableNew"/> Check only new libraries
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="enableAll"/> Force check all libraries
            <props:radioButtonProperty name="org.whitesource.overrideCheckPolicies" value="disable"/> Disable
            <span class="smallNote">
                Optional. Override check policies in global settings.
            </span>
        </td>
    </tr>
    <tr id="containerOverrideForceUpdate" style="${showSettings ? '' : 'display: none;'}">
        <th>
            Force update:
            <bs:helpIcon iconTitle="<b>Use global settings</b> - Override this property from the global configuration.<br/>
                            <b>Force update</b> - Updates organization inventory regardless of policy violations.<br/>
                            <b>Update</b> - Updates inventory only when the newly introduced open source libraries conform with
                    organization policies."/>
        </th>
        <td>
            <props:radioButtonProperty name="org.whitesource.overrideForceUpdate" value="global"/> Use Global Settings
            <props:radioButtonProperty name="org.whitesource.overrideForceUpdate" value="forceUpdate"/> Force update
            <props:radioButtonProperty name="org.whitesource.overrideForceUpdate" value="update"/> Update
            <span class="smallNote">
                Optional. Override force update in global settings.
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
    <tr id="containerIncludes" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.includes">
                Filesets to include:
            </label>
        </th>
        <td>
            <props:multilineProperty name="org.whitesource.includes" linkTitle="Filesets to include" rows="3" cols="60"/>
            <span class="smallNote">
                Comma or line separated list of Ant-style filesets to include in update. Relative to checkout directory.
                Leave blank to use default file extensions from entire workspace.
            </span>
        </td>
    </tr>
    <tr id="containerExcludes" style="${showSettings ? '' : 'display: none;'}">
        <th>
            <label for="org.whitesource.excludes">
                Filesets to exclude:
            </label>
        </th>
        <td>
            <props:multilineProperty name="org.whitesource.excludes" linkTitle="Filesets to exclude" rows="3" cols="60"/>
            <span class="smallNote">
                Comma or line separated list of Ant-style filesets to exclude form update. Relative to checkout directory.
            </span>
        </td>
    </tr>

</l:settingsGroup>