package ru.msu.cmc.alumnihub.config.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

/**
 * Ensures an initial administrator exists on first start (all environments),
 * so a fresh production database has a usable login. Credentials come from
 * {@code ADMIN_EMAIL} / {@code ADMIN_PASSWORD}. Does nothing if an admin already
 * exists.
 */
@Component
@Order(1)
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public AdminBootstrapRunner(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.countByRole(Role.ADMIN) > 0) {
            return;
        }
        AppProperties.Admin admin = appProperties.admin();
        if (admin == null || !StringUtils.hasText(admin.email()) || !StringUtils.hasText(admin.password())) {
            log.warn("No admin exists and ADMIN_EMAIL/ADMIN_PASSWORD are not set — skipping admin bootstrap.");
            return;
        }
        String email = admin.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(admin.password()));
        user.setRole(Role.ADMIN);
        user.setEnabled(true);
        userRepository.save(user);
        log.info("Bootstrapped initial admin account: {}", email);
    }
}
