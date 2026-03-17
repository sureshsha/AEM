package com.aemtraining.core.services;

public interface GoogleAuthenticationService {
    GoogleUser verifyToken(String credential) throws Exception;

    String getSuccessRedirectPath();
}
