package com.example.unittestingsample.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * @author Niharika.Arora
 */
public class UtilityClass {
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);
    private static final int MIN_PASSWORD_LENGTH = 6;


    public static boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return EMAIL.matcher(email).matches();
        }
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        } else {
            return password.trim().length() >= MIN_PASSWORD_LENGTH;
        }
    }
}
