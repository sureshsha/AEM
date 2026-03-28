package com.aemtraining.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/training/querybuilder",
                "sling.servlet.methods=GET"
        }
)
public class QueryBuilderServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(QueryBuilderServlet.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Build the query parameters
        Map<String, String> map = new HashMap<>();
        map.put("path", "/content/wknd"); // Root path to search
        map.put("type", "cq:Page");       // Node type to search
        map.put("1_property", "jcr:content/cq:template"); // Property filter
        map.put("1_property.value", "/conf/wknd/settings/wcm/templates/landing-page-template");       // Property value to match
        map.put("p.limit", "-1");          // Get all results, -1 means unlimited

        Session session = request.getResourceResolver().adaptTo(Session.class);

        try {
            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();

            StringBuilder sb = new StringBuilder();
            sb.append("{ \"pages\": [");

            boolean first = true;
            for (Hit hit : result.getHits()) {
                if (!first) sb.append(",");
                sb.append("{\"path\":\"").append(hit.getPath()).append("\"}");
                first = false;
            }
            sb.append("] }");

            response.getWriter().write(sb.toString());

        } catch (Exception e) {
            LOG.error("Error executing query", e);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
