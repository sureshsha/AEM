package com.aemtraining.core.services;

public class GoogleUser {
    private final String name;
    private final String email;
    private final String aud;

    public GoogleUser(String name, String email, String aud) {
        this.name = name;
        this.email = email;
        this.aud = aud;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAud() {
        return aud;
    }
}
