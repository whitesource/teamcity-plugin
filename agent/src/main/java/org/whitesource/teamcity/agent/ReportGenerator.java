package org.whitesource.teamcity.agent;

import jetbrains.buildServer.util.FileUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.whitesource.agent.api.dispatch.CheckPoliciesResult;
import org.whitesource.agent.api.model.PolicyCheckResourceNode;
import org.whitesource.agent.api.model.RequestPolicyInfo;
import org.whitesource.agent.api.model.ResourceInfo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

/**
 * @author Edo.Shor
 */
public class ReportGenerator {

    /* --- Static members --- */

    private static final String LOG_COMPONENT = "ReportGenerator";

    public ReportGenerator() {

    }

    public void generatePolicyRejectionsReport(CheckPoliciesResult result, File directory) throws IOException {
        Velocity.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();

        VelocityContext context = new VelocityContext();
        context.put("buildName", "build name goes here");
        context.put("buildNumber", "132");
        context.put("creationTime", SimpleDateFormat.getInstance().format(new Date()));
        context.put("result", result);
        context.put("hasRejections", result.hasRejections());
        FileWriter fw = new FileWriter(new File(directory, "index.html"));
        Velocity.mergeTemplate("templates/policy-check.vm", "UTF-8", context, fw);

        fw.flush();
        fw.close();
    }
}
