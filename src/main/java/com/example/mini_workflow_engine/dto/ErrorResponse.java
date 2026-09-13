package com.example.mini_workflow_engine.dto;

import java.time.Instant;

// Structured JSON returned whenever a request fails.
// Example:
// {
//   "timestamp": "2026-09-13T10:15:30Z",
//   "status": 400,
//   "error": "INVALID_TRANSITION",
//   "message": "Invalid transition: submit"
// }
public class ErrorResponse {

    // When the error happened (set automatically, see constructor below)
    private Instant timestamp;

    // The HTTP status code, e.g. 400 or 403
    private int status;

    // A short machine-readable error code, e.g. "INVALID_REQUEST" or "FORBIDDEN"
    private String error;

    // A human-readable explanation of what went wrong
    private String message;

    // Empty constructor needed for Spring to build this object when
    // converting it to JSON
    public ErrorResponse() {
    }

    // Constructor used everywhere an error is actually thrown —
    // timestamp is always set to "right now" automatically
    public ErrorResponse(int status, String error, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
