package com.aemtraining.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class AccordionModel {

    /**
     * Outer multifield node: "./groups"
     */
    @ChildResource
    private List<GroupRow> groups;

    public List<GroupRow> getGroups() {
        return groups != null ? Collections.unmodifiableList(groups) : Collections.emptyList();
    }

    /**
     * Outer Multifield Row
     */
    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class GroupRow {

        @ValueMapValue
        private String title;

        @ValueMapValue
        private String description;


        /**
         * Inner multifield node: "./items"
         */
        @ChildResource
        private List<ItemRow> items;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public List<ItemRow> getItems() {
            return items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
        }
    }

    /**
     * Inner Multifield Row
     */
    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class ItemRow {

        @ValueMapValue
        private String question;

        @ValueMapValue
        private String answer;

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
