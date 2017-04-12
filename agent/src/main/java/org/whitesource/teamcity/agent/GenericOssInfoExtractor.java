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
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.whitesource.agent.api.ChecksumUtils;
import org.whitesource.agent.api.model.AgentProjectInfo;
import org.whitesource.agent.api.model.Coordinates;
import org.whitesource.agent.api.model.DependencyInfo;
import org.whitesource.teamcity.common.WssUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Concrete implementation for generic job types.
 * Based on user entered locations of open source libraries.
 *
 * @author Edo.Shor
 */
public class GenericOssInfoExtractor extends BaseOssInfoExtractor {

    /* --- Static members --- */

    private static final String LOG_COMPONENT = "GenericExtractor";

    private static final List<String> DEFAULT_SCAN_EXTENSIONS = new ArrayList<String>();
    static {
        DEFAULT_SCAN_EXTENSIONS.addAll(
                Arrays.asList("jar", "war", "ear", "par", "rar",
                        "dll", "exe", "ko", "so", "msi",
                        "zip", "tar", "tar.gz",
                        "swc", "swf"));
    }

    /* --- Members --- */

    protected List<Pattern> includePatterns;

    protected List<Pattern> excludePatterns;

    /* --- Constructors --- */

    /**
     * Constructor
     *
     * @param runner
     */
    public GenericOssInfoExtractor(BuildRunnerContext runner) {
        super(runner);

        includePatterns = new ArrayList<Pattern>();
        for (String pattern : includes) {
            includePatterns.add(Pattern.compile(FileUtil.convertAntToRegexp(pattern)));
        }
        if (includePatterns.isEmpty()) {
            for (String extension : DEFAULT_SCAN_EXTENSIONS) {
                includePatterns.add(Pattern.compile(FileUtil.convertAntToRegexp("**/*." + extension)));
            }
        }

        excludePatterns = new ArrayList<Pattern>();
        for (String pattern : excludes) {
            excludePatterns.add(Pattern.compile(FileUtil.convertAntToRegexp(pattern)));
        }

    }

    /* --- Concrete implementation methods --- */

    @Override
    public Collection<AgentProjectInfo> extract() {
        Loggers.AGENT.info(WssUtils.logMsg(LOG_COMPONENT, "Collection started"));

        final AgentRunningBuild build = runner.getBuild();

        // we send something anyhow, even when no OSS found.
        Collection<AgentProjectInfo> projectInfos = new ArrayList<AgentProjectInfo>();
        AgentProjectInfo projectInfo = new AgentProjectInfo();
        projectInfos.add(projectInfo);

        projectInfo.setCoordinates(new Coordinates(null, build.getProjectName(), null));
        projectInfo.setProjectToken(projectToken);

        BuildProgressLogger buildLogger = build.getBuildLogger();
        if (includePatterns.isEmpty()) {
            Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, "No include patterns defined. Skipping."));
            buildLogger.warning("No include patterns defined. Can't look for open source information.");
        } else {
            buildLogger.message("Including files matching:");
            buildLogger.message(StringUtil.join(includes, "\n"));
            if (excludes.isEmpty()) {
                buildLogger.message("Excluding none.");
            } else {
                buildLogger.message("Excluding files matching:");
                buildLogger.message(StringUtil.join(excludes, "\n"));
            }

            extractOssInfo(build.getCheckoutDirectory(), projectInfo.getDependencies());
        }

        return projectInfos;
    }

    /* --- Private methods --- */

    private void extractOssInfo(final File root, final Collection<DependencyInfo> dependencyInfos) {
        extractOssInfo(root, root, dependencyInfos);
    }

    private void extractOssInfo(final File absoluteRoot, final File root, final Collection<DependencyInfo> dependencyInfos) {
        final File[] files = root.listFiles();
        if (files == null){
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                final String path = FileUtil.toSystemIndependentName(FileUtil.getRelativePath(absoluteRoot, file));

                boolean process = matchAny(path, includePatterns);
                if (process) {
                    process = !matchAny(path, excludePatterns);
                }

                if (process) {
                    dependencyInfos.add(extractDependencyInfo(file));
                }
            } else {
                extractOssInfo(absoluteRoot, file, dependencyInfos);
            }
        }
    }

    private boolean matchAny(String value, List<Pattern> patterns) {
        boolean match = false;

        Iterator<Pattern> it = patterns.iterator();
        while (it.hasNext() && !match) {
            match = it.next().matcher(value).matches();
        }

        return match;
    }

    private DependencyInfo extractDependencyInfo(File file) {
        DependencyInfo info = new DependencyInfo();

        info.setSystemPath(file.getAbsolutePath());
        info.setArtifactId(file.getName());

        try {
            info.setSha1(ChecksumUtils.calculateSHA1(file));
            info.setOtherPlatformSha1(ChecksumUtils.calculateOtherPlatformSha1(file));
        } catch (IOException e) {
            String msg = "Error calculating SHA-1 for " + file.getAbsolutePath();
            Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, msg));
            runner.getBuild().getBuildLogger().message(msg);
        }

        return info;
    }

}
