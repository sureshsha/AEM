package com.aemtraining.core.models;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.REQUIRED)
public class AemTraining {

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String content;

    public String getText() {
        return text;
    }

    public String getContent() {
        return content;
    }




}
