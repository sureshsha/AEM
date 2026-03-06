package com.aemtraining.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/createpagejcr",
                "sling.servlet.methods=GET"
        }
)
public class CreatePageUsingJcrServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        ResourceResolver resolver = request.getResourceResolver();

        try {
            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                response.getWriter().write("Session is null");
                return;
            }

            Node parentNode = session.getNode("/content/aemtraining/us/en");

            Node pageNode = parentNode.addNode("jcr-demo-page", "cq:Page");
            Node contentNode = pageNode.addNode("jcr:content", "cq:PageContent");

            contentNode.setProperty("jcr:title", "JCR Demo Page");
            contentNode.setProperty("sling:resourceType", "aemtraining/components/page");
            contentNode.setProperty("cq:template", "/conf/aemtraining/settings/wcm/templates/content-page");

            session.save();

            response.getWriter().write("Page created using JCR at: " + pageNode.getPath());

        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
