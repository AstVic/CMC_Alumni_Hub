package ru.msu.cmc.alumnihub.config;

import org.junit.jupiter.api.Test;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductionConfigurationValidatorTest {

    @Test
    void acceptsStrongProductionConfiguration() {
        AppProperties properties = properties(
                "a".repeat(64), "Strong-production-password-42", "https://alumni.example.com");

        assertDoesNotThrow(() -> new ProductionConfigurationValidator(properties).validate());
    }

    @Test
    void rejectsShortJwtSecret() {
        AppProperties properties = properties(
                "too-short", "Strong-production-password-42", "https://alumni.example.com");

        assertThrows(IllegalStateException.class,
                () -> new ProductionConfigurationValidator(properties).validate());
    }

    @Test
    void rejectsDefaultJwtSecret() {
        AppProperties properties = properties(
                ProductionConfigurationValidator.DEV_JWT_SECRET,
                "Strong-production-password-42",
                "https://alumni.example.com");

        assertThrows(IllegalStateException.class,
                () -> new ProductionConfigurationValidator(properties).validate());
    }

    @Test
    void rejectsWeakAdminPassword() {
        AppProperties properties = properties(
                "b".repeat(64), "admin123", "https://alumni.example.com");

        assertThrows(IllegalStateException.class,
                () -> new ProductionConfigurationValidator(properties).validate());
    }

    @Test
    void rejectsWildcardOrPathInFrontendOrigin() {
        AppProperties wildcard = properties(
                "c".repeat(64), "Strong-production-password-42", "*");
        AppProperties path = properties(
                "c".repeat(64), "Strong-production-password-42", "https://example.com/app");

        assertThrows(IllegalStateException.class,
                () -> new ProductionConfigurationValidator(wildcard).validate());
        assertThrows(IllegalStateException.class,
                () -> new ProductionConfigurationValidator(path).validate());
    }

    private AppProperties properties(String secret, String password, String frontendUrl) {
        return new AppProperties(
                frontendUrl,
                false,
                new AppProperties.Admin("admin@example.com", password),
                new AppProperties.Jwt(secret, 60, 7),
                new AppProperties.Invite(7),
                new AppProperties.Mail("no-reply@example.com"),
                new AppProperties.Storage("./uploads"),
                new AppProperties.Moderation("rule-based", "", ""));
    }
}
