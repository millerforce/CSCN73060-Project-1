package com.group1.froggy.app.controllers;

import com.group1.froggy.api.error.ProblemDetailFactory;
import com.group1.froggy.api.error.ValidationDetail;
import com.group1.froggy.api.error.ValidationProcessor;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centralized exception handler for controllers.
 *
 * <p>Maps common application and validation exceptions to appropriate HTTP
 * responses. Most handlers return a ProblemDetail or ValidationDetail so the
 * API responses are consistent.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle entity-not-found errors and return a 404 ProblemDetail.
     *
     * @param e the exception indicating the entity was not found
     * @return a ProblemDetail with HTTP 404 status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail entityNotFoundException(EntityNotFoundException e) {
        return ProblemDetailFactory.createProblemDetail(HttpStatus.NOT_FOUND, e);
    }

    /**
     * Handle entity-exists errors and return a 409 ProblemDetail.
     *
     * @param e the exception indicating the entity already exists
     * @return a ProblemDetail with HTTP 409 status
     */
    @ExceptionHandler(EntityExistsException.class)
    public ProblemDetail entityExistsException(EntityExistsException e) {
        return ProblemDetailFactory.createProblemDetail(HttpStatus.CONFLICT, e);
    }

    /**
     * Handle invalid credentials and return a 401 ProblemDetail.
     *
     * <p>We intentionally use 401 (Unauthorized) to avoid exposing whether the
     * resource exists.</p>
     *
     * @param e the invalid credentials exception
     * @return a ProblemDetail with HTTP 401 status
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail invalidCredentialsException(InvalidCredentialsException e) {
        // We could throw a 403 Forbidden here, but 401 Unauthorized hides the existence of the resource
        return ProblemDetailFactory.createProblemDetail(HttpStatus.UNAUTHORIZED, e);
    }

    /**
     * Handle illegal actions (permission errors) and return a 403 ProblemDetail.
     *
     * @param e the exception indicating the action is forbidden
     * @return a ProblemDetail with HTTP 403 status
     */
    @ExceptionHandler(IllegalActionException.class)
    public ProblemDetail illegalActionException(IllegalActionException e) {
        return ProblemDetailFactory.createProblemDetail(HttpStatus.FORBIDDEN, e);
    }

    /**
     * Handle bean validation constraint violations and return a 400
     * ValidationDetail describing the problems.
     *
     * @param ex the constraint violation exception
     * @return ResponseEntity containing a ValidationDetail and HTTP 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        ValidationDetail body = ProblemDetailFactory.createValidationDetail();
        ValidationProcessor.addConstraintViolationsToValidationDetail(ex.getConstraintViolations(), body);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handle MethodArgumentNotValidException (typically from @Valid on request
     * bodies) and convert it to a ValidationDetail response.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        ValidationDetail body = ProblemDetailFactory.createValidationDetail(ex.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale()));
        ValidationProcessor.addErrorsToValidationDetail(ex.getGlobalErrors(), ex.getFieldErrors(), body);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * Handle HandlerMethodValidationException (validation on controller method
     * parameters) and convert it to a ValidationDetail response.
     */
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        ValidationDetail body = ProblemDetailFactory.createValidationDetail(ex.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale()));
        ValidationProcessor.addValidationResultsToValidationDetail(ex.getAllValidationResults(), body);
        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
