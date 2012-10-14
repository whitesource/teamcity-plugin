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
package org.whitesource.teamcity.agent;

import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.whitesource.agent.api.ChecksumUtils;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.agent.api.model.Coordinates;
import org.whitesource.agent.api.model.DependencyInfo;
import org.whitesource.teamcity.common.Constants;
import org.whitesource.teamcity.common.WssUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Concrete implementation for maven job type.
 * Based on teamcity's report for maven projects.
 *
 * @author Edo.Shor
 */
public class MavenOssInfoExtractor extends BaseOssInfoExtractor {

    /* --- Static members --- */

    private static final String MAVEN_BUILD_INFO_XML = "maven-build-info.xml";

    private static final String LOG_COMPONENT = "MavenExtractor";

    /* --- Members --- */

    protected Map<String, String> moduleTokens;

    protected boolean ignorePomModules;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param runner
     */
    public MavenOssInfoExtractor(BuildRunnerContext runner) {
        super(runner);

        ignorePomModules = Boolean.parseBoolean(
                runner.getRunnerParameters().get(Constants.RUNNER_IGNORE_POM_MODULES));
        moduleTokens = WssUtils.splitParametersMap(
                runner.getRunnerParameters().get(Constants.RUNNER_MODULE_TOKENS));

    }

    /* --- Concrete implementation methods --- */

    @Override
    public Collection<AgentProjectInfo> extract() {
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "Collection started"));

        Collection<AgentProjectInfo> projectInfos = new ArrayList<AgentProjectInfo>();

        final AgentRunningBuild build = runner.getBuild();

        // find and maven build info report
        File mavenBuildInfoFile = new File(build.getBuildTempDirectory(), MAVEN_BUILD_INFO_XML);
        if (!mavenBuildInfoFile.exists()) {
            String missingMavenInfo = "Can't find maven build info report.";
            Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, missingMavenInfo));
            build.getBuildLogger().warning(missingMavenInfo);
            throw new RuntimeException("Error collecting maven information, Skipping update.");
        }

        // parse maven info report and extract OSS usage information
        try {
            Element root = FileUtil.parseDocument(mavenBuildInfoFile);

            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, xmlOutputter.outputString(root)));

            Map<String, Coordinates> hierarchy = new HashMap<String, Coordinates>();
            processHierarchy(root.getChild("hierarchy"), hierarchy);

            Element projects = root.getChild("projects");
            if (projects != null) {
                List<Element> projectList = projects.getChildren("project");
                for (Element projectElement : projectList) {
                    if (!shouldProcess(projectElement)) {
                        continue;
                    }

                    String groupId = projectElement.getChildText("groupId");
                    String artifactId = projectElement.getChildText("artifactId");
                    String version = projectElement.getChildText("version");

                    build.getBuildLogger().message("Processing " + artifactId);

                    AgentProjectInfo projectInfo = new AgentProjectInfo();
                    projectInfo.setCoordinates(new Coordinates(groupId, artifactId, version));
                    projectInfo.setParentCoordinates(hierarchy.get(projectElement.getChildText("id")));

                    if (projectList.size() == 1) {
                        projectInfo.setProjectToken(projectToken);
                    } else {
                        projectInfo.setProjectToken(moduleTokens.get(artifactId));
                    }

                    Element dependencyArtifacts = projectElement.getChild("dependencyArtifacts");
                    if (dependencyArtifacts != null) {
                        List<Element> dependencyList = dependencyArtifacts.getChildren("artifact");
                        for (Element dependencyElement : dependencyList) {
                            if (isDirectDependency(dependencyElement)) {
                                DependencyInfo info = new DependencyInfo();

                                info.setGroupId(dependencyElement.getChildText("groupId"));
                                info.setArtifactId(dependencyElement.getChildText("artifactId"));
                                info.setVersion(dependencyElement.getChildText("version"));
                                info.setClassifier(dependencyElement.getChildText("classifier"));
                                info.setType(dependencyElement.getChildText("type"));
                                info.setScope(dependencyElement.getChildText("scope"));

                                String dependencyPath = dependencyElement.getChildText("path");
                                if (!StringUtil.isEmptyOrSpaces(dependencyPath)) {
                                    info.setSystemPath(dependencyPath);
                                    try {
                                        info.setSha1(ChecksumUtils.calculateSHA1(new File(dependencyPath)));
                                    } catch (IOException e) {
                                        Loggers.AGENT.debug("Unable to calculate SHA-1 for " + dependencyPath);
                                    }
                                }

                                projectInfo.getDependencies().add(info);
                            }
                        }
                    }

                    build.getBuildLogger().message("Found " + projectInfo.getDependencies().size() + " direct dependencies");
                    projectInfos.add(projectInfo);
                }
            }
        } catch (JDOMException e) {
            throw new RuntimeException("Error parsing maven information.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading maven information.", e);
        }

        return projectInfos;
    }


    /* --- Private methods --- */

    private void processHierarchy(Element root, Map<String, Coordinates> hierarchy) {
        if (root == null) {
            return;
        }

        List<Element> nodes = root.getChildren("node");
        for(Element node : nodes) {
            Coordinates parentCoordinates= idToCoordinates(node.getChildText("id"));
            Element children = node.getChild("children");
            List<Element> childrenNodes = children.getChildren("node");
            for (Element child : childrenNodes) {
                hierarchy.put(child.getChildText("id"), parentCoordinates);
                processHierarchy(child, hierarchy);
            }
        }
    }

    private Coordinates idToCoordinates(String id) {
        Coordinates coordinates = null;

        String[] split = id.split(":");
        if (split.length > 3) {
            coordinates = new Coordinates(split[0], split[1], split[3]);
        }

        return coordinates;
    }

    private boolean isDirectDependency(Element dependencyElement) {
        // check the dependency trail to see only two dependents.
        // Expecting actual module and self for direct dependencies.
        return dependencyElement.getChild("dependencyTrail").getChildren("id").size() == 2;
    }

    private boolean shouldProcess(Element project) {
        boolean process = true;

        String artifactId = project.getChildText("artifactId");
        String packaging = project.getChildText("packaging");

        if (ignorePomModules && "pom".equals(packaging)) {
            process = false;
        } else if (!excludes.isEmpty() && matchAny(artifactId, excludes)) {
            process = false;
        } else if (!includes.isEmpty() && matchAny(artifactId, includes)) {
            process = true;
        }

        return process;
    }

    private boolean matchAny(String value, List<String> patterns) {
        boolean match = false;

        for (String pattern : patterns) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            if (value.matches(regex)) {
                match = true;
                break;
            }
        }

        return match;
    }
}
