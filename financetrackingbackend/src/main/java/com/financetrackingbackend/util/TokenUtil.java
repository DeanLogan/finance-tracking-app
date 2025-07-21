package com.financetrackingbackend.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class TokenUtil {
    public static String generateStateToken() {
        return generateStateToken(new SecureRandom());
    }

    public static String generateStateToken(SecureRandom secureRandom) {
        try {
            byte[] randomBytes = new byte[32];
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
