package org.whitesource.teamcity.common;

/**
 * @author Edo.Shor
 */
public final class WssUtils {

    public static String logMsg(String component, String msg) {
        return "[whitesource]::" + component + ": " + msg;
    }

    public static boolean isMavenRunType(String runType) {
        return "Maven2".equals(runType);
    }
}
