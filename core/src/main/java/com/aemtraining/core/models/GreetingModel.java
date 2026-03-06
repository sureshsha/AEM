package com.aemtraining.core.models;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Design;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class GreetingModel {
    private static final Logger logger = LoggerFactory.getLogger(GreetingModel.class);


    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private Resource resource;

    public String getCurrentPage() {
        logger.info("pageTitle {}", currentPage.getPageTitle());
        logger.info("PageName: {}", currentPage.getName());
        return currentPage.getPageTitle() != null ? currentPage.getPageTitle() : currentPage.getName();
    }

    public String getPageInfo() {
        return resource.getPath();
    }


    public String getGreetingMessage() {

        String name = request.getParameter("name");

        if (name != null && !name.trim().isEmpty()) {
            return "Hello " + name + " 👋";
        }

        return "Hello Guest 👋";
    }
}



