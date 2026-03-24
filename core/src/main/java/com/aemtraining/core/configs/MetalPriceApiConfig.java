package com.aemtraining.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MetalpriceAPI Scheduler Config")
public @interface MetalPriceApiConfig {

    @AttributeDefinition(name = "Enable Scheduler")
    boolean enabled() default true;

    @AttributeDefinition(name = "Cron Expression")
    String cronExpression() default "0 0/30 * * * ?";

    @AttributeDefinition(name = "Scheduler Name")
    String schedulerName() default "Gold Silver Rate Scheduler";

    @AttributeDefinition(name = "API Key")
    String apiKey() default "08d43f1cc6ab89e795bc57d6d73a75b2";

    @AttributeDefinition(name = "Base Currency")
    String base() default "INR";

    @AttributeDefinition(name = "Currencies")
    String currencies() default "XAU,XAG";

    @AttributeDefinition(name = "Storage Path")
    String storagePath() default "/var/aemtraining/metal-rates/india";
}
