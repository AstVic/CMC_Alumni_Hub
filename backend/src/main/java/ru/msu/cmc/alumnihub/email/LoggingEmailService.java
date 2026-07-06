package ru.msu.cmc.alumnihub.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Development fallback used ONLY when SMTP is not configured (local/demo).
 * It prints the full email — including one-time links — to the console so
 * invite and password-reset flows can be exercised without a mail server.
 *
 * <p>This is a "console mailbox" dev pattern. In production SMTP must be
 * configured (see DEPLOYMENT.md), so this fallback — and any token in logs —
 * never occurs there.
 */
public class LoggingEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void send(String to, String subject, String body) {
        log.warn("""

                ===== EMAIL (SMTP not configured — DEV console fallback) =====
                To:      {}
                Subject: {}
                -------------------------------------------------------------
                {}
                =============================================================
                """, to, subject, body);
    }
}
