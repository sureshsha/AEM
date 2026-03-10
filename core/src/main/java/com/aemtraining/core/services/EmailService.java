package com.aemtraining.core.services;

public interface EmailService {
    boolean sendEmail(String to, String subject, String message);
}