package com.aemtraining.core.configs;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AEM Training - Google Login Configuration",
        description = "Configuration for Google Login integration"
)
public @interface GoogleAuthenticationConfig {
    @AttributeDefinition(
            name = "Google Client ID",
            description = "OAuth client ID from Google Cloud Console"
    )
    String googleClientId() default "";
    @AttributeDefinition(
            name = "Google Token Verification URL",
            description = "Google endpoint used to verify ID token"
    )
    String tokenVerificationUrl() default "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @AttributeDefinition(
            name = "Success Redirect Path",
            description = "Page to redirect after successful login"
    )
    String successRedirectPath() default "/content/aemtraining/us/en/thankyou.html";

}
