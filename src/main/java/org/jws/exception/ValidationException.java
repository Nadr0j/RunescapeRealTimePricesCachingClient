/* (C)2024 */
package org.jws.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
