package com.coreinvader.ciar.io;

import java.io.IOException;

import org.json.JSONArray;

/**
 * General {@link IOException} that indicates a problem occured while
 * parsing or applying a {@link JSONArray}.
 */
public class JsonHandlerException extends IOException {
    
    private static final long serialVersionUID = 5375121183325949546L;

    public JsonHandlerException(String message) {
        super(message);
    }

    public JsonHandlerException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    @Override
    public String toString() {
        if (getCause() != null) {
            return getLocalizedMessage() + ": " + getCause();
        } else {
            return getLocalizedMessage();
        }
    }
    
}
