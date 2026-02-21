package com.aemtraining.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class GalleryModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ChildResource(name = "images")
    private Resource images;

    private List<ImageItem> imageList = new ArrayList<>();

    @PostConstruct
    protected void init() {

        if (images != null) {
            for (Resource child : images.getChildren()) {

                String src = child.getValueMap().get("src", "");
                String caption = child.getValueMap().get("caption", "");

                imageList.add(new ImageItem(src, caption));
            }
        }
    }

    public String getTitle() {
        return title != null ? title : "Gallery Showcase";
    }

    public String getDescription() {
        return description != null
                ? description
                : "A premium image gallery component built in AEM.";
    }

    public List<ImageItem> getImages() {
        return imageList.isEmpty() ? null : imageList;
    }

    // Inner POJO
    public static class ImageItem {

        private final String src;
        private final String caption;

        public ImageItem(String src, String caption) {
            this.src = src;
            this.caption = caption;
        }

        public String getSrc() {
            return src;
        }

        public String getCaption() {
            return caption;
        }
    }
}
