package com.yourname.zerotrust.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TokenHashUtilTest {

    private final TokenHashUtil tokenHashUtil = new TokenHashUtil();

    @Test
    void hash_producesConsistentSha256Hex() {
        String hash1 = tokenHashUtil.hash("my-refresh-token");
        String hash2 = tokenHashUtil.hash("my-refresh-token");

        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    void hash_differsForDifferentTokens() {
        String hash1 = tokenHashUtil.hash("token-a");
        String hash2 = tokenHashUtil.hash("token-b");

        assertNotEquals(hash1, hash2);
    }
}
