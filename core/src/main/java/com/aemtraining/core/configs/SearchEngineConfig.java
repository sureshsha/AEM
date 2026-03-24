package com.aemtraining.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Search Engine Configurations",
        description = "Search engine Description")
public @interface SearchEngineConfig {

    @AttributeDefinition(name = "Site Identifier")
    String[] site() default {
        "/content/en",
            "/content/us"
    };
    @AttributeDefinition(name = "Sites Identifier")
    String sites() default "default value";

    @AttributeDefinition(name = "checkbox")
    boolean checkbox() default false;

    @AttributeDefinition(name = "checkbox")
    int number() default 1;

}
