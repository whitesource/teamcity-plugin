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
    public final static String RUNNER_PROXY_HOST = PLUGIN_PREFIX + "proxyHost";
    public final static String RUNNER_PROXY_PORT = PLUGIN_PREFIX + "proxyPort";
    public final static String RUNNER_PROXY_USERNAME = PLUGIN_PREFIX + "proxyUsername";
    public final static String RUNNER_PROXY_PASSWORD = PLUGIN_PREFIX + "proxyPassword";
    public final static String RUNNER_SERVICE_URL = PLUGIN_PREFIX + "serviceUrl";

}
