package ru.msu.cmc.alumnihub.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.msu.cmc.alumnihub.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Sends real emails over SMTP.
 */
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailService(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully");
        } catch (Exception ex) {
            log.error("SMTP delivery failed ({})", ex.getClass().getSimpleName());
            log.debug("SMTP delivery failure details", ex);
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Не удалось отправить письмо");
        }
    }
}
