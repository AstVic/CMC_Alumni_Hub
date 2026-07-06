package ru.msu.cmc.alumnihub.invite.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generates cryptographically strong, one-time invite tokens and hashes them.
 *
 * <p>The raw token (Base64URL of 32 random bytes) is emailed to the alumni; only
 * its SHA-256 hash is persisted, so a database leak cannot reveal usable links.
 */
@Component
public class InviteTokenGenerator {

    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();

    /** A fresh random raw token to be placed into the invite link. */
    public String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return urlEncoder.encodeToString(bytes);
    }

    /** Deterministic SHA-256 hash (hex) used for storage and lookup. */
    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
