package org.whitesource.teamcity.agent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.whitesource.agent.api.dispatch.CheckPoliciesResult;
import org.whitesource.agent.api.model.ResourceInfo;

/**
 * @author Edo.Shor
 */
public class ReportGenerator {

    /* --- Static members --- */

    private static final String LOG_COMPONENT = "ReportGenerator";

    private static final float BAR_HEIGHT = 50;
	
	private static final int LICENSE_LIMIT = 6;
	
	private static final String OTHER_LICENSE = "Other types";
    
	/* --- Constructors --- */
	
    public ReportGenerator() {

    }

    /* --- Public methods --- */
    
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
        context.put("licenses", createLicensesDetails(result));
        FileWriter fw = new FileWriter(new File(directory, "index.html"));
        Velocity.mergeTemplate("templates/policy-check.vm", "UTF-8", context, fw);

        fw.flush();
        fw.close();
    }
    
    /* --- Private methods --- */
    
   	private Collection<LicenseDetails> createLicensesDetails(CheckPoliciesResult result) {
   		Collection<LicenseDetails> licenseDetails = new ArrayList<LicenseDetails>();
   		
   		// get all licenses
   		Map<String, Integer> licenseHistogram = new HashMap<String, Integer>();
   		for (Entry<String, Collection<ResourceInfo>> entry : result.getProjectNewResources().entrySet()) {
   			for (ResourceInfo resource : entry.getValue()) {
   				getResourceLicenses(licenseHistogram, resource);
   			}
   		}
   		
   		// sort licenses
   		List<Entry<String, Integer>> sortedLicenses = getSortedList(licenseHistogram);
   		
   		// set max occurrences
   		float maxOccurrences = 0;
   		if (!sortedLicenses.isEmpty()) {
   			maxOccurrences = sortedLicenses.get(0).getValue();
   		}
   		
   		// calculate height
   		float factor = BAR_HEIGHT / maxOccurrences;
   		int index = 0;
   		int otherLicenses = 0;
   		for (Entry<String, Integer> entry : sortedLicenses) {
   			if (index == LICENSE_LIMIT) {
   				// add to 'other' licenses
   				otherLicenses += entry.getValue();
   			} else {
   				// add license to chart
   				LicenseDetails details = new LicenseDetails();
   				details.setName(entry.getKey());
   				details.setHeight((int)(entry.getValue() * factor));
   				details.setOccurrences(entry.getValue());
   				
   				licenseDetails.add(details);
   				index++;
   			}
   		}
   		
   		// check 'other' licenses
   		if (otherLicenses > 0) {
   			LicenseDetails details = new LicenseDetails();
   			details.setName(OTHER_LICENSE + " (" + (sortedLicenses.size() - LICENSE_LIMIT) + ")");
   			details.setOccurrences(otherLicenses);
   			details.setHeight((int)(otherLicenses * factor));
   			licenseDetails.add(details);
   		}
   		
   		return licenseDetails;
   	}

   	private List<Entry<String, Integer>> getSortedList(Map<String, Integer> licenseHistogram) {
   		// sort by descending license occurrences
   		List<Entry<String, Integer>> licenses = new ArrayList<Map.Entry<String,Integer>>(licenseHistogram.entrySet());
   		Collections.sort(licenses, new Comparator<Entry<String, Integer>>() {

   			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
   				return o2.getValue().compareTo(o1.getValue());
   			}
   		});
   		
   		return licenses;
   	}

   	private void getResourceLicenses(Map<String, Integer> licensHistogram, ResourceInfo resource) {
   		for (String license : resource.getLicenses()) {
   			Integer licenseCount = licensHistogram.get(license);
   			if (licenseCount == null) {
   				licenseCount = 0;
   			}
   			licensHistogram.put(license, ++licenseCount);
   		}
   	}
}
