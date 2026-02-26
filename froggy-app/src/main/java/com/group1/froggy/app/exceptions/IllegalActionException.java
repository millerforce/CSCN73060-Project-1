package com.group1.froggy.app.exceptions;

/**
 * Thrown when the current user attempts an action they are not permitted to perform.
 *
 * <p>Examples: attempting to edit or delete another user's post/comment, or performing an
 * administrative operation without sufficient privileges.</p>
 *
 * <p>This is an unchecked exception intended to be thrown by service-layer methods (for
 * example, methods in {@link com.group1.froggy.app.services.PostService} or
 * {@link com.group1.froggy.app.services.CommentService}) when business logic disallows the
 * requested operation.</p>
 *
 * <p>Global handling: mapped to HTTP 403 Forbidden by
 * {@link com.group1.froggy.app.controllers.GlobalExceptionHandler#illegalActionException(IllegalActionException)}</p>
 */
public class IllegalActionException extends RuntimeException {
    public IllegalActionException(String message) {
        super(message);
    }
}
