package org.whitesource.teamcity.common;

/**
 * Utility class to hold constant values used throughout the plugin.
 *
 * @author Edo.Shor
 */
public final class Constants {

    /* --- Plugin constants --- */
    public final static String PLUGIN_NAME = "whitesource";
    public final static String AGENT_TYPE = "teamcity";
    public final static String AGENT_VERSION = "1.0";

    /* --- Runner parameters --- */
    public final static String PLUGIN_PREFIX = "org.whitesource.";
    public final static String RUNNER_DO_UPDATE = PLUGIN_PREFIX + "doUpdate";
    public final static String RUNNER_ORGANIZATION_TOKEN = PLUGIN_PREFIX + "orgToken";
    public final static String RUNNER_OVERRIDE_ORGANIZATION_TOKEN = PLUGIN_PREFIX + "overrideOrgToken";
    public final static String RUNNER_CHECK_POLICIES = PLUGIN_PREFIX + "checkPolicies";
    public final static String RUNNER_OVERRIDE_CHECK_POLICIES = PLUGIN_PREFIX + "overrideCheckPolicies";
    public final static String RUNNER_PROJECT_TOKEN = PLUGIN_PREFIX + "projectToken";
    public final static String RUNNER_MODULE_TOKENS = PLUGIN_PREFIX + "moduleTokens";
    public final static String RUNNER_INCLUDES = PLUGIN_PREFIX + "includes";
    public final static String RUNNER_EXCLUDES = PLUGIN_PREFIX + "excludes";
    public final static String RUNNER_IGNORE_POM_MODULES = PLUGIN_PREFIX + "ignorePomModules";
    public final static String RUNNER_PROXY_HOST = PLUGIN_PREFIX + "proxyHost";
    public final static String RUNNER_PROXY_PORT = PLUGIN_PREFIX + "proxyPort";
    public final static String RUNNER_PROXY_USERNAME = PLUGIN_PREFIX + "proxyUsername";
    public final static String RUNNER_PROXY_PASSWORD = PLUGIN_PREFIX + "proxyPassword";
    public final static String RUNNER_SERVICE_URL = PLUGIN_PREFIX + "serviceUrl";


    /* --- Runner types --- */
    public static final String RUNNER_MAVEN = "Maven2";
    public static final String RUNNER_ANT = "Ant";
    public static final String RUNNER_CMD = "simpleRunner";
    public static final String RUNNER_MSBUILD = "MSBuild";
    public static final String RUNNER_POWERSHELL = "jetbrains_powershell";
    public static final String RUNNER_IDEA = "JPS";
    public static final String RUNNER_GRADLE = "gradle-runner";

}
