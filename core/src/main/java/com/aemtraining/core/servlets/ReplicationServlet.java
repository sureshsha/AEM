package com.aemtraining.core.servlets;


import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component(service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/replication",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET
        })

public class ReplicationServlet extends SlingSafeMethodsServlet {

    @Reference
    ResourceResolverFactory factory;

    @Reference
    private Replicator replicator;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {

        final Map<String, Object> authInfo = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                "systemuser");


        try (ResourceResolver resolver = factory.getServiceResourceResolver(authInfo)) {

            // adapting the resolver into a javax.jcr.Session.class
            Session session = resolver.adaptTo(Session.class);

            // create node obj to perform operations using JCR API
            Node contentNode = session.getNode("/content/aemtraining/us/en/jcr:content");

            /**
             * writing new properties to /content/myproject/jcr:content,
             and replicating the node.
             */
            if (Objects.nonNull(contentNode)) {
                contentNode.setProperty("testing", "data-added");
                session.save();
              //  replicator.replicate(session, ReplicationActionType.ACTIVATE, contentNode.getPath());
                resp.setStatus(200);
                resp.getWriter().write("Data is saved and is replicated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("System failed to save.");
        }
    }
}