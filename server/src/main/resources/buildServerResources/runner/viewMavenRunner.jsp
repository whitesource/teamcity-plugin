<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<c:set var="doUpdate" value="${not empty propertiesBean.properties['org.whitesource.doUpdate']}" />
<c:set var="overrideOrgToken" value="${propertiesBean.properties['org.whitesource.overrideOrgToken']}" />
<c:set var="projectToken" value="${propertiesBean.properties['org.whitesource.projectToken']}" />
<c:set var="moduleTokens" value="${propertiesBean.properties['org.whitesource.moduleTokens']}" />
<c:set var="includes" value="${propertiesBean.properties['org.whitesource.includes']}" />
<c:set var="excludes" value="${propertiesBean.properties['org.whitesource.excludes']}" />
<c:set var="ignorePomModules" value="${propertiesBean.properties['org.whitesource.ignorePomModules']}" />

<div class="parameter">
    Update White Source:
    <strong>
        <c:out value="${doUpdate ? 'enabled' : 'disabled'}"/>
    </strong>

    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Organization token:
        <strong>
            <c:out value="${empty overrideOrgToken ? 'from global configuration' : overrideOrgToken}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate and not empty projectToken ? '' : 'display:none;'}">
        Project token:
        <strong>
            <c:out value="${projectToken}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate and not empty moduleTokens ? '' : 'display:none;'}">
        Module tokens:
        <strong>
            <c:out value="${moduleTokens}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Modules to include:
        <strong>
            <c:out value="${includes ? includes : 'All modules'}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Modules to exclude:
        <strong>
            <c:out value="${excludes ? excludes : 'No modules'}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Ignore pom modules:
        <strong>
            <c:out value="${ignorePomModules ? 'true' : 'false'}"/>
        </strong>
    </div>
</div>