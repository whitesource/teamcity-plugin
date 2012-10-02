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

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Setup a tab for the policy check report.
 *
 * @author Edo.Shor
 */
public class PolicyCheckReportTab extends ViewLogTab {

    /* --- Static members --- */

    private static final String TAB_TITLE = "White Source";
    private static final String TAB_CODE = "whitesourceReportTab";
    private static final String TAB_BASEPATH = "whitesource.zip";
    private static final String TAB_STARTPAGE = "index.html";

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param pagePlaces
     * @param server
     */
    public PolicyCheckReportTab(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server) {
        super(TAB_TITLE, TAB_CODE, pagePlaces, server);
        setIncludeUrl("/artifactsViewer.jsp");
    }

    /* --- Concrete implementation methods --- */

    @Override
    protected void fillModel(Map model, HttpServletRequest request, @Nullable SBuild build) {
        model.put("basePath", TAB_BASEPATH);
        model.put("startPage", TAB_STARTPAGE);
    }
}
