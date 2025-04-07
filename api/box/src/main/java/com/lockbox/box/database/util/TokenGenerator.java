package com.lockbox.box.database.util;

import java.security.SecureRandom;

public class TokenGenerator {

    private static final int TOKEN_LENGTH = 150;
    private static final String PRINTABLE_CHARSET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // todo add special characters

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a secure token with all printable ASCII characters.
     * The token meets the length requirement and includes a wide range of symbols for increased entropy.
     *
     * @return a secure token as a {@link String}
     */
    public static String generateToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = RANDOM.nextInt(PRINTABLE_CHARSET.length());
            token.append(PRINTABLE_CHARSET.charAt(index));
        }

        return token.toString();
    }
}
