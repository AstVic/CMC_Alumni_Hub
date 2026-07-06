package ru.msu.cmc.alumnihub.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

/**
 * Picks the email implementation at startup: real SMTP when a mail host is
 * configured, otherwise the logging fallback.
 */
@Configuration
public class EmailConfig {

    private static final Logger log = LoggerFactory.getLogger(EmailConfig.class);

    @Bean
    public EmailService emailService(MailProperties mailProperties,
                                     AppProperties appProperties,
                                     ObjectProvider<JavaMailSender> mailSenderProvider,
                                     Environment environment) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (StringUtils.hasText(mailProperties.getHost()) && sender != null) {
            log.info("SMTP configured (host={}). Using real email delivery.", mailProperties.getHost());
            return new SmtpEmailService(sender, appProperties.mail().from());
        }
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            log.error("SMTP is not configured in production. Emails will NOT be delivered.");
        } else {
            log.warn("SMTP host not configured. Emails will be suppressed by the safe fallback.");
        }
        return new LoggingEmailService();
    }
}
