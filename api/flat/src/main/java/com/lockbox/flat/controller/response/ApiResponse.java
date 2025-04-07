package com.lockbox.flat.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * A class representing a standardized response structure for HTTP requests.
 * This class provides methods to create both successful and error responses,
 * encapsulating the response body, error flag, and an optional error message.
 * <p>
 * The {@code Response} record is a simple structure with two fields:
 * <ul>
 *     <li>{@code error} - a boolean flag indicating whether the response represents an error.</li>
 *     <li>{@code errorText} - a string containing the error message, only present when {@code error} is true.</li>
 * </ul>
 * <p>
 * The nested {@link Body} class wraps the actual response body, along with the error flag and error message.
 * This structure ensures that both successful and error responses follow the same format, enhancing consistency
 * in API design.
 *
 * <p><b>Usage:</b></p>
 * The static methods in this class can be used to quickly generate responses with specific HTTP statuses,
 * error states, and optional error messages. This pattern can be useful in REST APIs where error handling and
 * response formatting should follow a unified structure.
 *
 * @param error     a flag indicating whether the response is an error
 * @param errorText an optional message describing the error, null if {@code error} is false
 * @version 1.0
 * @since 1.0
 */
public record ApiResponse(boolean error, String errorText) {
    /**
     * Creates a successful response with the given body and a 200 OK status.
     *
     * @param body the body of the response, representing the payload
     * @param <T>  the type of the response body
     * @return a {@link ResponseEntity} with an HTTP 200 OK status and the provided body wrapped in a {@link Body}
     */
    public static <T> ResponseEntity<Body<T>> ok(T body) {
        return ResponseEntity
                .ok(new Body<>(body));
    }

    /**
     * Creates a bad request response with the given body and error message, returning an HTTP 400 Bad Request status.
     *
     * @param body      the body of the response, representing the payload
     * @param errorText the error message to be included in the response
     * @param <T>       the type of the response body
     * @return a {@link ResponseEntity} with an HTTP 400 Bad Request status, the provided body,
     * and an error message wrapped in a {@link Body} object
     */
    public static <T> ResponseEntity<Body<T>> bad(T body, String errorText) {
        return ResponseEntity
                .badRequest()
                .body(new Body<>(body, true, errorText));
    }

    /**
     * Creates a bad request response with only an error message, returning an HTTP 400 Bad Request status.
     *
     * @param errorText the error message to be included in the response
     * @param <T>       the type of the response body
     * @return a {@link ResponseEntity} with an HTTP 400 Bad Request status and an error message wrapped
     * in a {@link Body} object
     */
    public static <T> ResponseEntity<Body<T>> bad(String errorText) {
        return ResponseEntity
                .badRequest()
                .body(new Body<>(null, true, errorText));
    }

    /**
     * Creates an error response with the given error message and HTTP status code.
     *
     * @param errorText the error message to be included in the response
     * @param status    the HTTP status to be returned
     * @param <T>       the type of the response body
     * @return a {@link ResponseEntity} with the specified HTTP status and an error message wrapped in a {@link Body}
     */
    public static <T> ResponseEntity<Body<T>> bad(String errorText, HttpStatusCode status) {
        return ResponseEntity
                .status(status)
                .body(new Body<>(null, true, errorText));
    }

    /**
     * Creates an error response with the given error message and HTTP status code (int format).
     *
     * @param errorText the error message to be included in the response
     * @param status    the HTTP status as an integer value
     * @param <T>       the type of the response body
     * @return a {@link ResponseEntity} with the specified HTTP status and an error message wrapped in a {@link Body}
     */
    public static <T> ResponseEntity<Body<T>> bad(String errorText, int status) {
        return ResponseEntity
                .status(status)
                .body(new Body<>(null, true, errorText));
    }

    /**
     * Creates an error response with only an HTTP status code, without an error message.
     *
     * @param status the HTTP status to be returned
     * @param <T>    the type of the response body
     * @return a {@link ResponseEntity} with the specified HTTP status and an empty body wrapped in a {@link Body}
     */
    public static <T> ResponseEntity<Body<T>> bad(HttpStatusCode status) {
        return ResponseEntity
                .status(status)
                .body(new Body<>(null, true, null));
    }

    /**
     * Creates an error response with only an HTTP status code (int format), without an error message.
     *
     * @param status the HTTP status as an integer value
     * @param <T>    the type of the response body
     * @return a {@link ResponseEntity} with the specified HTTP status and an empty body wrapped in a {@link Body}
     */
    public static <T> ResponseEntity<Body<T>> bad(int status) {
        return ResponseEntity
                .status(status)
                .body(new Body<>(null, true, null));
    }

    /**
     * A class representing the response body, including the main payload, error flag, and error message.
     *
     * @param <T> the type of the response body
     */
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    public static class Body<T> {
        private T body;
        private boolean error;
        private String errorText;

        /**
         * Constructs a new {@link Body} instance representing a successful response (no error).
         *
         * @param body the body of the response
         */
        public Body(T body) {
            this(body, false, null);
        }
    }
}
