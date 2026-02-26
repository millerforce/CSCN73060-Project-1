package com.group1.froggy.app.exceptions;

/**
 * Thrown when authentication fails for the current operation.
 *
 * <p>Examples: incorrect password during login, missing or invalid session cookie/token when
 * accessing an authenticated endpoint.</p>
 *
 * <p>This is an unchecked exception used by services such as
 * {@link com.group1.froggy.app.services.AuthorizationService},
 * {@link com.group1.froggy.app.services.PostService}, and
 * {@link com.group1.froggy.app.services.CommentService}.</p>
 *
 * <p>Global handling: mapped to HTTP 401 Unauthorized by
 * {@link com.group1.froggy.app.controllers.GlobalExceptionHandler#invalidCredentialsException(InvalidCredentialsException)}</p>
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
