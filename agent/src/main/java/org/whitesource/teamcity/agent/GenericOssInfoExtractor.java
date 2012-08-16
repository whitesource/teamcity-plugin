package org.whitesource.teamcity.agent;

import jetbrains.buildServer.agent.AgentRunningBuild;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

        if (includePatterns.isEmpty()) {
            Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, "No include patterns defined. Skipping."));
            build.getBuildLogger().warning("No include patterns defined. Can't look for open source information.");
        } else {
            build.getBuildLogger().message("Including files matching:");
            build.getBuildLogger().message(StringUtil.join(includes,"\n"));
            if (excludes.isEmpty()) {
                build.getBuildLogger().message("Excluding none.");
            } else {
                build.getBuildLogger().message("Excluding files matching:");
                build.getBuildLogger().message(StringUtil.join(excludes,"\n"));
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
                    dependencyInfos.add(extractDepependencyInfo(file));
                }
            } else {
                extractOssInfo(absoluteRoot, file, dependencyInfos);
            }
        }
    }

    private boolean matchAny(String value, List<Pattern> patterns) {
        boolean match = false;

        for (Pattern pattern : patterns) {
            if (pattern.matcher(value).matches()) {
                match = true;
                break;
            }
        }

        return match;
    }

    private DependencyInfo extractDepependencyInfo(File file) {
        DependencyInfo info = new DependencyInfo();

        info.setSystemPath(file.getAbsolutePath());
        info.setArtifactId(file.getName());

        try {
            info.setSha1(ChecksumUtils.calculateSHA1(file));
        } catch (IOException e) {
            String msg = "Error calculating SHA-1 for " + file.getAbsolutePath();
            Loggers.AGENT.warn(WssUtils.logMsg(LOG_COMPONENT, msg));
            runner.getBuild().getBuildLogger().message(msg);
        }

        return info;
    }

}
