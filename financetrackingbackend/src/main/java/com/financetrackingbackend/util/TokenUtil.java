package com.financetrackingbackend.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class TokenUtil {
    public static String generateStateToken() {
        try {
            byte[] randomBytes = new byte[32];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateStateToken(String providedToken, String expectedToken) {
        if (providedToken == null || expectedToken == null) {
            return false;
        }
        return providedToken.equals(expectedToken);
    }
}
