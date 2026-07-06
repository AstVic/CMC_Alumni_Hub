package ru.msu.cmc.alumnihub.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.auth.dto.ForgotPasswordRequest;
import ru.msu.cmc.alumnihub.auth.dto.ResetPasswordRequest;
import ru.msu.cmc.alumnihub.auth.entity.PasswordResetToken;
import ru.msu.cmc.alumnihub.auth.repository.PasswordResetTokenRepository;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;
import ru.msu.cmc.alumnihub.email.EmailService;
import ru.msu.cmc.alumnihub.invite.service.InviteTokenGenerator;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * "Forgot password" flow: emails a one-time reset link and applies a new
 * password. Only the token hash is stored; the request step never reveals
 * whether an email is registered (anti-enumeration).
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final long EXPIRY_MINUTES = 60;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final InviteTokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                InviteTokenGenerator tokenGenerator,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder,
                                AppProperties appProperties) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.frontendUrl = appProperties.frontendUrl();
    }

    @Transactional
    public void requestReset(ForgotPasswordRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElse(null);
        // Anti-enumeration: respond the same whether or not the user exists.
        if (user == null || !user.isEnabled()) {
            log.info("Password reset requested for unknown/disabled email — no email sent.");
            return;
        }

        tokenRepository.invalidateActiveForUser(user.getId());

        String rawToken = tokenGenerator.generateRawToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getId());
        token.setTokenHash(tokenGenerator.hash(rawToken));
        token.setExpiresAt(Instant.now().plus(EXPIRY_MINUTES, ChronoUnit.MINUTES));
        tokenRepository.save(token);

        emailService.send(user.getEmail(), "Восстановление пароля — CMC Alumni Hub", body(rawToken));
        log.info("Password reset link sent to userId={}", user.getId());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByTokenHash(tokenGenerator.hash(request.token()))
                .orElseThrow(() -> new BadRequestException("Ссылка недействительна"));
        if (token.getUsedAt() != null) {
            throw new BadRequestException("Ссылка уже была использована");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Срок действия ссылки истёк");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BadRequestException("Пользователь не найден"));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        token.setUsedAt(Instant.now());
        tokenRepository.invalidateActiveForUser(user.getId());
        log.info("Password reset completed for userId={}", user.getId());
    }

    private String body(String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;
        return """
                Здравствуйте!

                Мы получили запрос на восстановление пароля для вашего аккаунта в CMC Alumni Hub.

                Чтобы задать новый пароль, перейдите по ссылке:

                %s

                Ссылка действительна 1 час.

                Если вы не запрашивали восстановление пароля, просто проигнорируйте это письмо —
                ваш пароль останется прежним.

                С уважением,
                команда CMC Alumni Hub
                """.formatted(link);
    }
}
