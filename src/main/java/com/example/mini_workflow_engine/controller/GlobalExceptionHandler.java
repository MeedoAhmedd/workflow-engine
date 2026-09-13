package com.example.mini_workflow_engine.controller;

import com.example.mini_workflow_engine.dto.ErrorResponse;
import com.example.mini_workflow_engine.service.ForbiddenActionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice means: this class doesn't handle any specific URL
// itself, but instead catches exceptions thrown by ANY controller in the
// app and decides how to turn them into an HTTP response. Without this,
// an uncaught exception would surface as an ugly generic HTTP 500 error
// with no useful JSON body.
@ControllerAdvice
public class GlobalExceptionHandler {

    // Whenever any controller method (or anything it calls) throws an
    // IllegalArgumentException — used throughout the services for
    // "invalid input" style problems — this method catches it here
    // instead of letting it crash the request.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),   // 400
                "INVALID_REQUEST",
                exception.getMessage()            // reuses whatever message the code threw
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // Same idea, but for permission denials specifically — kept separate
    // from IllegalArgumentException so a "you're not allowed to do this"
    // error comes back as 403 Forbidden instead of 400 Bad Request.
    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenActionException(
            ForbiddenActionException exception
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),   // 403
                "FORBIDDEN",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }
}
