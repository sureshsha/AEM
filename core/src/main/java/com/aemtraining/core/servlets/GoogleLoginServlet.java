package com.aemtraining.core.servlets;

import com.aemtraining.core.services.GoogleAuthenticationService;
import com.aemtraining.core.services.GoogleUser;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/aemtraining/googlelogin",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST
        }
)

public class GoogleLoginServlet extends SlingAllMethodsServlet {

    @Reference
    GoogleAuthenticationService googleAuthenticationService;

    private static final String CLIENT_ID = "948331957795-1a8bt6nedej4jg6co72q5mjfctl55qrl.apps.googleusercontent.com";

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String credential = request.getParameter("credential");
            GoogleUser googleUser = googleAuthenticationService.verifyToken(credential);

            request.getSession(true).setAttribute("googleUserName", googleUser.getName());
            request.getSession().setAttribute("googleUserEmail", googleUser.getEmail());
            request.getSession().setAttribute("isGoogleLoggedIn", true);

            String json = "{"
                    + "\"success\":true,"
                    + "\"name\":\"" + escape(googleUser.getName()) + "\","
                    + "\"redirectPath\":\"" + escape(googleAuthenticationService.getSuccessRedirectPath()) + "\""
                    + "}";

            response.getWriter().write(json);

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.getWriter().write("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        } catch (SecurityException e) {
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}