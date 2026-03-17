package com.aemtraining.core.servlets;

import com.aemtraining.core.services.EmailService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.servlets.annotations.SlingServletPaths;


import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/sendtestemail")
public class SendTestEmailServlet extends SlingSafeMethodsServlet {

    @Reference
    private EmailService emailService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
String firstName = request.getParameter("fname");
String lastName = request.getParameter("lname");
        boolean sent = emailService.sendEmail(
                "",
                "Welcome to AEM Trainings",
                "Hi "+firstName + "Welcome to AEM Trainings."
        );

        response.setContentType("text/plain");
        response.getWriter().write(sent ? "Email sent successfully" : "Email sending failed");
    }
}