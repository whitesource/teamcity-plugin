<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<script type="text/javascript">

    function toggleSettings() {
        $('containerOverrideOrgToken').toggle();
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
                <br/>For exmaple, type "test-*" to exclude all artifactIds prefixed with "test-".
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