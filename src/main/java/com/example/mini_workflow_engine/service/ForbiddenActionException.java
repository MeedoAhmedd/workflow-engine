package com.example.mini_workflow_engine.service;

// Thrown when a caller is correctly authenticated/scoped (right ownerId)
// but lacks the role required to execute a specific transition. Kept
// distinct from IllegalArgumentException so it maps to HTTP 403 instead
// of 400 — this isn't a malformed request, it's a permission denial.
public class ForbiddenActionException extends RuntimeException {

    public ForbiddenActionException(String message) {
        super(message);
    }
}
