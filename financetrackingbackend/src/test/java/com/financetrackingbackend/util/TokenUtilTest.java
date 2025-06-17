package com.financetrackingbackend.util;

import org.junit.jupiter.api.Test;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TokenUtilTest {
    private static final String TOKEN = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8";

    @Test
    void generateStateToken_success() {
        SecureRandom secureRandom = mock(SecureRandom.class);
        doAnswer(invocation -> {
            byte[] bytes = invocation.getArgument(0);
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) i;
            }
            return null;
        }).when(secureRandom).nextBytes(any(byte[].class));

        String tokenResult = TokenUtil.generateStateToken(secureRandom);

        assertEquals(TOKEN, tokenResult);
        verify(secureRandom).nextBytes(any(byte[].class));
    }

    @Test
    void generateStateToken_tokenNotNull() {
        String tokenResult = TokenUtil.generateStateToken();
        assertNotNull(tokenResult);
    }

    @Test
    void generateStateToken_catchesError() {
        SecureRandom secureRandom = mock(SecureRandom.class);
        doAnswer(invocation -> { throw new NullPointerException(); })
            .when(secureRandom).nextBytes(any(byte[].class));
    
        String token = TokenUtil.generateStateToken(secureRandom);
    
        assertNull(token);
        verify(secureRandom).nextBytes(any(byte[].class));
    }

    @Test
    void validateStateToken_trueWhenStateTokensAreTheSame() {
        boolean valid = TokenUtil.validateStateToken(TOKEN, TOKEN);
        assertTrue(valid);
    }

    @Test
    void validateStateToken_falseWhenStateTokensAreDifferent() {
        boolean valid = TokenUtil.validateStateToken(TOKEN, "");
        assertFalse(valid);
    }

    @Test
    void validateStateToken_falseWhenOneIsNull() {
        boolean valid = TokenUtil.validateStateToken(TOKEN, null);
        assertFalse(valid);
    }

    @Test
    void validateStateToken_falseWhenBothIsNull() {
        boolean valid = TokenUtil.validateStateToken(null, null);
        assertFalse(valid);
    }
}