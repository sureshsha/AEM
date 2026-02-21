package com.aemtraining.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class Slide {

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String heading;

    @ValueMapValue
    private String subtext;

    @ValueMapValue
    private String buttonLabel;

    @ValueMapValue
    private String buttonLink;

    public String getImage() {
        return image;
    }

    public String getHeading() {
        return heading;
    }

    public String getSubtext() {
        return subtext;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getButtonLink() {
        return buttonLink;
    }
}
