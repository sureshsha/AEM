package com.aemtraining.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Arrays;


@Component(service = WorkflowProcess.class, property = { "process.label=" + "Process Step Example" })
public class CustomWorkflow implements WorkflowProcess {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        //Get Workflow Data
        WorkflowData workflowData = workItem.getWorkflowData();

        //Getting payload from Workflow
        String payloadType = workflowData.getPayloadType();

        try {
            // Check type of payload; there are two - JCR_PATH and JCR_UUID
            if (StringUtils.equals(payloadType, "JCR_PATH")) {
                log.info("Payload type: {}", payloadType);

                Session session = workflowSession.adaptTo(Session.class);
                // Get the JCR path from the payload
                String path = workItem.getWorkflowData().getPayload().toString();
                log.info("Payload path: {}", path);

                Node node = (Node) session.getItem(path);
                node.setProperty("property", "value");

                //To get the MetaDataMap
                MetaDataMap workFlowMetaDataMap = workItem.getWorkflow().getWorkflowData().getMetaDataMap();

                // Get workflow process arguments
                String[] processArguments = metaDataMap.get("PROCESS_ARGS", "Default").split(",");
                log.info("Process args: {}", Arrays.toString(processArguments));
            }
        } catch (Exception e) {
            log.info("Exception Occurred:{}", e.getMessage());
        }
    }
}