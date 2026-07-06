package ru.msu.cmc.alumnihub.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fallback used when SMTP is not configured. Message content is deliberately
 * not logged because invitation bodies contain one-time authentication tokens.
 */
public class LoggingEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void send(String to, String subject, String body) {
        log.warn("Email suppressed because SMTP is not configured (recipientDomain={}, subject={})",
                recipientDomain(to), subject);
    }

    private String recipientDomain(String address) {
        int separator = address == null ? -1 : address.lastIndexOf('@');
        return separator >= 0 ? address.substring(separator + 1) : "unknown";
    }
}
