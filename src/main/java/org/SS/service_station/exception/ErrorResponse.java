package org.SS.service_station.exception;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {

    private int statusCode; // HTTP error status
    private String message; // Error message
    private LocalDateTime timestamp; // Time when the error occurred
    private Map<String, String> details; // Additional details (for example, validation error)
    private String stackTrace; // Stack trace for debugging

    // Constructor without details
    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = null;
    }

    // Constructor with details
    public ErrorResponse(int statusCode, String message, Map<String, String> details) {
        this(statusCode, message);
        this.details = details;
    }

    // Constructor with stack trace (for internal server errors)
    public ErrorResponse(int statusCode, String message, Throwable throwable) {
        this(statusCode, message);
        this.stackTrace = throwable.toString(); // Add stack trace for debugging
    }

}
