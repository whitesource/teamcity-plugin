package org.whitesource.teamcity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class to hold common utility helper methods.
 *
 * @author Edo.Shor
 */
public final class WssUtils {

    /* --- Static members --- */

    private static final Pattern PARAM_LIST_SPLIT_PATTERN = Pattern.compile(",|$", Pattern.MULTILINE);
    private static final Pattern KEY_VALUE_SPLIT_PATTERN = Pattern.compile("=");

    /* --- Public methods --- */

    public static String logMsg(String component, String msg) {
        return "[whitesource]::" + component + ": " + msg;
    }

    public static boolean isMavenRunType(String runType) {
        return Constants.RUNNER_MAVEN.equals(runType);
    }

    public static List<String> splitParameters(String paramList) {
        List<String> params = new ArrayList<String>();

        if (paramList != null) {
            String[] split = PARAM_LIST_SPLIT_PATTERN.split(paramList);
            if (split != null) {
                for (String param : split) {
                    if (!(param == null || param.trim().length() == 0)) {
                        params.add(param.trim());
                    }
                }
            }
        }

        return params;
    }

    public static Map<String, String> splitParametersMap(String paramList) {
        Map<String, String> params = new HashMap<String, String>();

        List<String> kvps = splitParameters(paramList);
        if (kvps != null) {
            for (String kvp : kvps) {
                String[] split = KEY_VALUE_SPLIT_PATTERN.split(kvp);
                if (split.length == 2) {
                    params.put(split[0], split[1]);
                }
            }
        }

        return params;
    }

}
