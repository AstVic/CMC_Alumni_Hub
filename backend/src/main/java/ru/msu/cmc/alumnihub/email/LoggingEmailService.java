package ru.msu.cmc.alumnihub.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fallback used when SMTP is not configured: logs the email (including the
 * invite link) so the project boots and can be demoed without real SMTP.
 */
public class LoggingEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("""

                ===== EMAIL (SMTP not configured, logging fallback) =====
                To:      {}
                Subject: {}
                ---------------------------------------------------------
                {}
                =========================================================
                """, to, subject, body);
    }
}
