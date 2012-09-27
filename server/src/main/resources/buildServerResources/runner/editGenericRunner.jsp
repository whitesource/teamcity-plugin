<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<script type="text/javascript">

    function toggleSettings() {
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

    <tr id="containerOverrideOrgToken" style="${showSettings ? '' : 'display: none;'}">
        <th>
            Override organization token:
            <bs:helpIcon iconTitle="Tokens can be found in the administration section of your account on White Source."/>
        </th>
        <td>
            <props:textProperty name="org.whitesource.overrideOrgToken" size="55"/>
            <span class="smallNote">
                Organization token to use instead of global settings.
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
                Override check policies in global settings.
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
                Unique identifier of the project to update. If omitted, default naming convention applies.
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