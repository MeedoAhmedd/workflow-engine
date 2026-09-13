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

    private Instant timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse() {
    }

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
