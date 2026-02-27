package com.aemtraining.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class GreetingModel {

    @Self
    private SlingHttpServletRequest request;

    public String getGreetingMessage() {

        String name = request.getParameter("name");

        if (name != null && !name.trim().isEmpty()) {
            return "Hello " + name + " 👋";
        }

        return "Hello Guest 👋";
    }
}