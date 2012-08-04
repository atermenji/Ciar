package com.coreinvader.ciar.util;

import java.util.regex.Pattern;

import android.content.ContentProvider;
import android.net.Uri;

import com.coreinvader.ciar.io.JsonHandler;

/**
 * Various utility methods used by {@link JsonHandler} implementations.
 */
public class HandlerUtils {
    
    /** Used to sanitize a string to be {@link Uri} safe. */
    private static final Pattern sSanitizePattern = Pattern.compile("[^a-z0-9-_]");
    private static final Pattern sParenPattern = Pattern.compile("\\(.*?\\)");
    
    public static String sanitizeId(String input) {
        return sanitizeId(input, false);
    }
    
    /**
     * Sanitize the given string to be {@link Uri} safe for building
     * {@link ContentProvider} paths.
     */
    public static String sanitizeId(String input, boolean stripParen) {
        if (input == null) return null;
        if (stripParen) {
            // Strip out all parenthetical statements when requested.
            input = sParenPattern.matcher(input).replaceAll("");
        }
        return sSanitizePattern.matcher(input.toLowerCase()).replaceAll("");
    }
}
