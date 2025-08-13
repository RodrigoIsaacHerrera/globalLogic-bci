package org.example.web.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.example.shared.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.security.SignatureException;
import java.time.LocalDateTime;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        String detail = "The requested resource: ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                detail.concat(HttpStatus.NOT_FOUND.name())
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String detail = "An unexpected error occurred  ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                detail.concat(HttpStatus.NOT_FOUND.name())
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String detail = "The HTTP verb is not supported for this endpoint. ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.METHOD_NOT_ALLOWED.value(),
                detail.concat(HttpStatus.METHOD_NOT_ALLOWED.name()));
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String detail = "The media type is not supported. ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                detail.concat(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name()));
        return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        String detail = "The media type is not acceptable. ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE.value(),
                detail.concat(HttpStatus.NOT_ACCEPTABLE.name()));
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException ex) {
        String detail = "Error authentication . ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                detail.concat(HttpStatus.FORBIDDEN.name()));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        String detail = "Error authentication . ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                detail.concat(HttpStatus.NOT_FOUND.name()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        String detail = "Error authentication TOKEN_EXPIRED. ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(), detail.concat(HttpStatus.UNAUTHORIZED.name()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ex) {
        String detail = "Error authentication MALFORMED_TOKEN. ";
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(), detail.concat(HttpStatus.UNAUTHORIZED.name()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }


}
