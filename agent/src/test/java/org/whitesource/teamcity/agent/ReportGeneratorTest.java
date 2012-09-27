package org.whitesource.teamcity.agent;

import org.junit.Test;
import org.whitesource.agent.api.dispatch.CheckPoliciesResult;
import org.whitesource.agent.api.model.PolicyCheckResourceNode;
import org.whitesource.agent.api.model.RequestPolicyInfo;
import org.whitesource.agent.api.model.ResourceInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Edo.Shor
 */
public class ReportGeneratorTest {

    @Test
    public void testGenerate() throws IOException {
        ReportGenerator reportGenerator = new ReportGenerator();

        File outputFile = new File("c:\\WhiteSource\\temp\\");

        CheckPoliciesResult result = new CheckPoliciesResult("Organization name");

        RequestPolicyInfo approvePolicy = new RequestPolicyInfo("Approve something");
        approvePolicy.setActionType("APPROVE");
//        RequestPolicyInfo rejectPolicy = new RequestPolicyInfo("Reject something");
//        rejectPolicy.setActionType("REJECT");

        RequestPolicyInfo rejectPolicy = approvePolicy;

        PolicyCheckResourceNode root = new PolicyCheckResourceNode();

        ResourceInfo resource = new ResourceInfo("resource-1");
        resource.setLicenses(Arrays.asList("GPL 3.0"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        root.getChildren().add(new PolicyCheckResourceNode(resource, rejectPolicy));

        resource = new ResourceInfo("resource-2");
        resource.setLicenses(Arrays.asList("MIT"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        root.getChildren().add(new PolicyCheckResourceNode(resource, null));

        resource = new ResourceInfo("resource-3");
        resource.setLicenses(Arrays.asList("Apache 2.0"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        root.getChildren().add(new PolicyCheckResourceNode(resource, approvePolicy));

        resource = new ResourceInfo("resource-4");
        resource.setLicenses(Arrays.asList("LGPL 2.1"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        PolicyCheckResourceNode node = new PolicyCheckResourceNode(resource, null);

        resource = new ResourceInfo("resource-44");
        resource.setLicenses(Arrays.asList("LGPL 3.0"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        node.getChildren().add(new PolicyCheckResourceNode(resource, rejectPolicy));

        resource = new ResourceInfo("resource-45");
        resource.setLicenses(Arrays.asList("LGPL 3.0"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        node.getChildren().add(new PolicyCheckResourceNode(resource, rejectPolicy));

        resource = new ResourceInfo("resource-46");
        resource.setLicenses(Arrays.asList("unknown"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        node.getChildren().add(new PolicyCheckResourceNode(resource, null));

        resource = new ResourceInfo("resource-47");
        resource.setLicenses(Arrays.asList("Apache 2.0"));
        resource.setLink("http://saas.whitesourcesoftware.com/Wss/WSS.html");
        node.getChildren().add(new PolicyCheckResourceNode(resource, approvePolicy));
        root.getChildren().add(node);


//        PolicyCheckResourceNode root = new PolicyCheckResourceNode();
//
//        root.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-1"), rejectPolicy));
//        root.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-2"), null));
//        root.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-3"), approvePolicy));
//
//        PolicyCheckResourceNode node = new PolicyCheckResourceNode(new ResourceInfo("resource-4"), null);
//        node.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-44"), rejectPolicy));
//        node.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-45"), rejectPolicy));
//        node.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-46"), null));
//        node.getChildren().add(new PolicyCheckResourceNode(new ResourceInfo("resource-47"), approvePolicy));
//        root.getChildren().add(node);

        result.getExistingProjects().put("Existing Project A", root);
        result.getExistingProjects().put("Existing Project B", root);
        result.getExistingProjects().put("Existing Project C", root);
        result.getExistingProjects().put("Existing Project D", root);
        result.getNewProjects().put("New Project A", root);
        result.getNewProjects().put("New Project B", root);

        reportGenerator.generatePolicyRejectionsReport(result, outputFile);

    }


}
