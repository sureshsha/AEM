package com.aemtraining.core.models;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.*;
import javax.inject.Inject;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardFlip {

    @Inject
    @Required
    private String name;

    @Inject
    @Optional
    private String field;

    @Inject
    @Default(values = "altText")
    private String alttext;

    @Inject
    private String fileReference;

    public String getName() {
        return name;
    }
    public String getField() {
        return field;
    }

    public String getAlttext() {
        return alttext;
    }

    public String getFileReference() {
        return fileReference;
    }
}
