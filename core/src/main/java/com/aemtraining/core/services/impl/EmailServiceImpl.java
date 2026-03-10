package com.aemtraining.core.services.impl;

import com.aemtraining.core.services.EmailService;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = EmailService.class, immediate = true)
public class EmailServiceImpl implements EmailService {

    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    public boolean sendEmail(String to, String subject, String message) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.addTo(to);
            email.setSubject(subject);
            email.setHtmlMsg("<html><body><h2>" + subject + "</h2><p>" + message + "</p></body></html>");
            email.setTextMsg(message);
            email.setFrom("no-reply@test.com");

            MessageGateway<HtmlEmail> gateway = messageGatewayService.getGateway(HtmlEmail.class);
            if (gateway != null) {
                gateway.send(email);
                return true;
            }
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return false;
    }
}