package com.aemtraining.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeroCarouselModel {

    /** Parent Multifield Node */
    @ChildResource(name = "slides")
    private Resource slidesResource;

    /** List of Slide Objects */
    private List<Slide> slides = new ArrayList<>();

    @PostConstruct
    protected void init() {

        if (slidesResource != null) {

            for (Resource child : slidesResource.getChildren()) {
                Slide slide = child.adaptTo(Slide.class);

                if (slide != null) {
                    slides.add(slide);
                }
            }
        }
    }

    public List<Slide> getSlides() {
        return slides;
    }
}
