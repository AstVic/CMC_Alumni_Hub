package ru.msu.cmc.alumnihub.email;

/**
 * Abstraction over email delivery. Implementations: real SMTP or a logging
 * fallback used when SMTP is not configured.
 */
public interface EmailService {

    void send(String to, String subject, String body);
}
