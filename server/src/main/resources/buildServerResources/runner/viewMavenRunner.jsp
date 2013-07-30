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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<c:set var="doUpdate" value="${not empty propertiesBean.properties['org.whitesource.doUpdate']}" />
<c:set var="product" value="${propertiesBean.properties['org.whitesource.product']}" />
<c:set var="productVersion" value="${propertiesBean.properties['org.whitesource.productVersion']}" />
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
        Product name or token:
        <strong>
            <c:out value="${product}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Product version:
        <strong>
            <c:out value="${productVersion}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Organization token:
        <strong>
            <c:out value="${empty overrideOrgToken ? 'from global configuration' : overrideOrgToken}"/>
        </strong>
    </div>
    <div class="nestedParameter" style="${doUpdate ? '' : 'display:none;'}">
        Check policies:
        <strong>
            <c:out value="${empty overrideCheckPolicies ? 'from global configuration' : overrideCheckPolicies}"/>
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