package com.aemtraining.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/createpageresource",
                "sling.servlet.methods=GET"
        }
)
public class CreatePageUsingResourceServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        ResourceResolver resolver = request.getResourceResolver();

        try {
            Resource parent = resolver.getResource("/content/aemtraining/us/en");

            if (parent == null) {
                response.getWriter().write("Parent resource not found");
                return;
            }

            Map<String, Object> pageProps = new HashMap<>();
            pageProps.put("jcr:primaryType", "cq:Page");

            Resource pageResource = resolver.create(parent, "resource-demo-page", pageProps);

            Map<String, Object> contentProps = new HashMap<>();
            contentProps.put("jcr:primaryType", "cq:PageContent");
            contentProps.put("jcr:title", "Resource API Demo Page");
            contentProps.put("sling:resourceType", "aemtraining/components/page");
            contentProps.put("cq:template", "/conf/aemtraining/settings/wcm/templates/content-page");

            resolver.create(pageResource, "jcr:content", contentProps);
            resolver.commit();

            response.getWriter().write("Page-like structure created using Resource API at: " + pageResource.getPath());

        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}