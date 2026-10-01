package com.exception;

import io.jsonwebtoken.JwtException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.DTO.ErrorHandlingResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorHandlingResponse> handleResourceNotFound(
            ResourceNotFoundException exception) {

        ErrorHandlingResponse response =
                ErrorHandlingResponse.builder()
                        .msg(exception.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(UserAlreadyExist.class)
    public ResponseEntity<ErrorHandlingResponse> handleUserAlreadyExist(
            UserAlreadyExist exception) {

        ErrorHandlingResponse response =
                ErrorHandlingResponse.builder()
                        .msg(exception.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorHandlingResponse> handleBadCredentials(
            BadCredentialsException exception) {

        ErrorHandlingResponse response =
                ErrorHandlingResponse.builder()
                        .msg("Invalid email or password")
                        .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorHandlingResponse> handleAccessDenied(
            AccessDeniedException exception) {

        ErrorHandlingResponse response =
                ErrorHandlingResponse.builder()
                        .msg("You are not authorized to perform this action")
                        .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorHandlingResponse> handleJwtException(
            JwtException exception) {

        ErrorHandlingResponse response =
                ErrorHandlingResponse.builder()
                        .msg("Invalid or expired JWT token")
                        .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHandlingResponse> handleGeneralException(
            Exception exception) {

        ErrorHandlingResponse response =
                ErrorHandlingResponse.builder()
                        .msg("An unexpected error occurred")
                        .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}