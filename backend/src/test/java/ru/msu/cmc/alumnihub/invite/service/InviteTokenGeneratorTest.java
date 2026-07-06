package ru.msu.cmc.alumnihub.invite.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InviteTokenGeneratorTest {

    private final InviteTokenGenerator generator = new InviteTokenGenerator();

    @Test
    void generatesRandomUrlSafeTokensAndStableSha256Hashes() {
        String first = generator.generateRawToken();
        String second = generator.generateRawToken();

        assertNotEquals(first, second);
        assertTrue(first.matches("[A-Za-z0-9_-]+"));
        assertEquals(64, generator.hash(first).length());
        assertEquals(generator.hash(first), generator.hash(first));
        assertNotEquals(first, generator.hash(first));
    }
}
