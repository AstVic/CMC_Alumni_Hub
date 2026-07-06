package ru.msu.cmc.alumnihub.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

/** Fails startup early when production is configured with unsafe credentials. */
@Component
@Profile("prod")
public class ProductionConfigurationValidator implements InitializingBean {

    static final String DEV_JWT_SECRET =
            "local-dev-secret-change-me-please-0123456789-abcdef";
    private static final Set<String> WEAK_ADMIN_PASSWORDS =
            Set.of("admin123", "password", "123456");

    private final AppProperties properties;

    public ProductionConfigurationValidator(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        validate();
    }

    void validate() {
        String jwtSecret = properties.jwt() == null ? null : properties.jwt().secret();
        if (!StringUtils.hasText(jwtSecret)) {
            fail("JWT_SECRET must be set in production");
        }
        if (DEV_JWT_SECRET.equals(jwtSecret)) {
            fail("JWT_SECRET must not use the local development default in production");
        }
        if (jwtSecret.length() < 64) {
            fail("JWT_SECRET must contain at least 64 characters in production");
        }

        String adminPassword = properties.admin() == null ? null : properties.admin().password();
        if (!StringUtils.hasText(adminPassword)) {
            fail("ADMIN_PASSWORD must be set in production");
        }
        if (WEAK_ADMIN_PASSWORDS.contains(adminPassword.strip().toLowerCase(Locale.ROOT))) {
            fail("ADMIN_PASSWORD uses a forbidden weak value in production");
        }
        if (adminPassword.length() < 12) {
            fail("ADMIN_PASSWORD must contain at least 12 characters in production");
        }

        validateFrontendOrigin(properties.frontendUrl());
    }

    private void validateFrontendOrigin(String origin) {
        if (!StringUtils.hasText(origin) || origin.contains("*")) {
            fail("APP_FRONTEND_URL must be an explicit HTTP(S) origin in production");
        }
        try {
            URI uri = new URI(origin);
            boolean validScheme = "https".equalsIgnoreCase(uri.getScheme())
                    || "http".equalsIgnoreCase(uri.getScheme());
            boolean originOnly = uri.getHost() != null
                    && (uri.getPath() == null || uri.getPath().isEmpty() || "/".equals(uri.getPath()))
                    && uri.getQuery() == null
                    && uri.getFragment() == null;
            if (!validScheme || !originOnly) {
                fail("APP_FRONTEND_URL must be an explicit HTTP(S) origin without a path");
            }
        } catch (URISyntaxException ex) {
            fail("APP_FRONTEND_URL is not a valid URI");
        }
    }

    private static void fail(String message) {
        throw new IllegalStateException("Invalid production configuration: " + message);
    }
}
