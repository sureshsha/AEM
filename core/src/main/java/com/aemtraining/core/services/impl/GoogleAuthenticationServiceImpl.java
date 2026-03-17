package com.aemtraining.core.services.impl;


import com.aemtraining.core.configs.GoogleAuthenticationConfig;
import com.aemtraining.core.services.GoogleAuthenticationService;
import com.aemtraining.core.services.GoogleUser;
import org.apache.commons.io.IOUtils;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component(service = GoogleAuthenticationService.class, immediate = true)
@Designate(ocd=GoogleAuthenticationConfig.class)
public class GoogleAuthenticationServiceImpl implements GoogleAuthenticationService {
    private String googleClientId;
    private String tokenVerificationUrl;
    private String successRedirectPath;
    @Activate
    @Modified
    protected void init(GoogleAuthenticationConfig config) {
        this.googleClientId = config.googleClientId();
        this.tokenVerificationUrl = config.tokenVerificationUrl();
        this.successRedirectPath = config.successRedirectPath();
    }

    @Override
    public GoogleUser verifyToken(String credential) throws Exception {
        if (credential == null || credential.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing credential");
        }

        String verifyUrl = tokenVerificationUrl + credential;

        HttpURLConnection connection = (HttpURLConnection) new URL(verifyUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int status = connection.getResponseCode();
        String responseBody = IOUtils.toString(
                status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream(),
                StandardCharsets.UTF_8
        );

        if (status != 200) {
            throw new IOException("Invalid Google token");
        }

        JSONObject json = new JSONObject(responseBody);

        String aud = json.optString("aud");
        String email = json.optString("email");
        String name = json.optString("name");

        if (!googleClientId.equals(aud)) {
            throw new SecurityException("Client ID mismatch");
        }

        if (name == null || name.isEmpty()) {
            name = (email != null && !email.isEmpty()) ? email : "User";
        }

        return new GoogleUser(name, email, aud);
    }

    @Override
    public String getSuccessRedirectPath() {
        return successRedirectPath;
    }
}
