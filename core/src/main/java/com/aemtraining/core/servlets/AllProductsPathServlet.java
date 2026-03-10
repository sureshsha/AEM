package com.aemtraining.core.servlets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;

@Component(
        service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/fakeapi",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class AllProductsPathServlet extends SlingSafeMethodsServlet {

    private static final String API_URL = "https://fakestoreapi.com/products";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpGet get = new HttpGet(API_URL);
            CloseableHttpResponse apiRes = client.execute(get);

            String json = EntityUtils.toString(apiRes.getEntity());

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            try {
                response.sendError(500, "Failed to fetch products: " + e.getMessage());
            } catch (Exception ignored) {}
        }
    }
}
