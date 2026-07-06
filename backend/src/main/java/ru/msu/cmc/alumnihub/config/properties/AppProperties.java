package ru.msu.cmc.alumnihub.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly-typed application configuration bound from the {@code app.*} tree.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String frontendUrl,
        Jwt jwt,
        Invite invite,
        Mail mail,
        Storage storage,
        Moderation moderation) {

    public record Jwt(
            String secret,
            long accessTokenTtlMinutes,
            long refreshTokenTtlDays) {
    }

    public record Invite(long expiryDays) {
    }

    public record Mail(String from) {
    }

    public record Storage(String uploadDir) {
    }

    public record Moderation(String provider, String aiApiUrl, String aiApiKey) {
    }
}
