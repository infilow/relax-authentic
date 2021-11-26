package com.infilos.auth.error;

import org.pac4j.core.exception.TechnicalException;

public class AuthorizeException extends TechnicalException {

    public AuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizeException(String message) {
        super(message);
    }

    public AuthorizeException(Throwable cause) {
        super(cause);
    }
}
