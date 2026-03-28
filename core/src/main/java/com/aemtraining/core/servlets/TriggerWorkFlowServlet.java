package com.aemtraining.core.servlets;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;


@Component(service = { Servlet.class })
@SlingServletPaths(value={"/mysite/triggerworkflow"})
public class TriggerWorkFlowServlet extends SlingSafeMethodsServlet {

    private final Logger LOG = LoggerFactory.getLogger(TriggerWorkFlowServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        final ResourceResolver resourceResolver = req.getResourceResolver();

        String returnStatus = "Workflow in progress";
        String payload = req.getRequestParameter("payload").getString();
        try{
            if(StringUtils.isNotBlank(payload)){
                WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);

                WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/request_for_deletion");
                WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH",payload);

                returnStatus = workflowSession.startWorkflow(workflowModel,workflowData).getState();
            }
        }catch (Exception e){
            LOG.info("Exception Occurred:{}",e.getMessage());
        }
        resp.setContentType("application/json");
        resp.getWriter().write(returnStatus);
    }
}