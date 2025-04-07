package com.lockbox.flat.controller.exc;

import com.lockbox.flat.controller.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * <p>
 * This class provides centralized exception handling for the entire Spring application, capturing and responding to
 * various types of exceptions. Each exception handler is responsible for logging the error and returning a standardized
 * response using the {@link ApiResponse} class.
 * <p>
 * By annotating this class with {@link ControllerAdvice}, it becomes active for all controllers in the application,
 * allowing for global exception handling.
 * <p>
 * The exceptions handled include validation errors, constraint violations, duplicate key exceptions, and uncaught exc.
 * Each handler logs the error details using Log4j2 and returns an appropriate HTTP status code with a corresponding
 * error message in the response.
 *
 * <h2>Order of Execution:</h2>
 * The {@code @Order(Ordered.HIGHEST_PRECEDENCE)} annotation ensures that this exception handler is given the highest
 * priority among any other exception handlers that may be present.
 *
 * <p><b>Usage:</b></p>
 * This class is designed to be automatically triggered whenever a relevant exception is thrown in the application,
 * simplifying error handling and improving code modularity.
 *
 * @version 1.0
 * @since 1.0
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
public final class GlobalExceptionHandler {

    /**
     * Handles validation exceptions thrown when method arguments are invalid.
     * <p>
     * Captures {@link MethodArgumentNotValidException}, which is typically thrown during validation of request bodies
     * annotated with {@code @Valid}. The method extracts field errors and returns them in a map, along with a
     * "method.argument.not.valid.exception" error message.
     * </p>
     *
     * @param methodArgumentNotValidException the exception to handle
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request status, containing field validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse.Body<Map<String, String>>> handleValidationExceptions(
            final MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        log.error("Validation error: ", methodArgumentNotValidException);
        return ApiResponse.bad(methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("unknown"),
                        (existing, replacement) -> existing,
                        HashMap::new
                )), "method.argument.not.valid.exception");
    }

    /**
     * Handles constraint violations, typically related to validation errors at the field level.
     * <p>
     * Captures {@link ConstraintViolationException}, which is thrown when a constraint on a method parameter or bean
     * property is violated. It collects the property path and message for each violation and returns them in a map.
     * </p>
     *
     * @param constraintViolations the exception to handle
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request status, containing constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse.Body<Map<String, String>>> handleConstraintViolationException(
            final ConstraintViolationException constraintViolations
    ) {
        log.error("Constraint violation: ", constraintViolations);
        return ApiResponse.bad(constraintViolations.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing,
                        HashMap::new
                )), "constraint.violation.exception");
    }

    /**
     * Handles web exchange bind exceptions, which occur when binding request data fails.
     * <p>
     * Captures {@link WebExchangeBindException}, which is typically thrown during reactive web request processing
     * when there are binding errors. It collects field errors and returns them in a map with an error message.
     * </p>
     *
     * @param e the exception to handle
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request status, containing field binding errors
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse.Body<Map<String, String>>> handleValidationException(
            final WebExchangeBindException e
    ) {
        log.error("Validation error: ", e);
        return ApiResponse.bad(e.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        v -> Optional.ofNullable(v.getDefaultMessage()).orElse("unknown"),
                        (existing, replacement) -> existing,
                        HashMap::new
                )), "web.exchange.bind.exception");
    }

    /**
     * Handles exceptions caused by duplicate keys in database operations.
     * <p>
     * Captures {@link DuplicateKeyException}, typically thrown when there is an attempt to insert a record
     * with a duplicate key (e.g., a unique constraint violation in the database). The exception message is returned
     * as part of the error response.
     * </p>
     *
     * @param e the exception to handle
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request status, containing the duplicate key error message
     */
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse.Body<String>> handleDuplicateKeyException(
            final DuplicateKeyException e
    ) {
        log.error("Duplicate key error: ", e);
        return ApiResponse.bad(e.getMessage(), "duplicate.key.exception");
    }

    /**
     * Handles all uncaught exceptions that are not explicitly handled by other exception handlers.
     * <p>
     * Captures {@link Exception}, the base class for all exceptions, and logs the error. The exception message is
     * returned as part of the error response. This handler returns a generic error message for internal server errors.
     * </p>
     *
     * @param e the uncaught exception to handle
     * @return a {@link ResponseEntity} with HTTP 500 Internal Server Error status, containing the exception message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse.Body<String>> handleAllUncaughtException(
            final Exception e
    ) {
        log.error("Uncaught exception: ", e);
        return ApiResponse.bad(e.getMessage(), "uncaught.exception"); // todo: adjust for production environment
    }
}
