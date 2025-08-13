package org.example.web.controller;

import org.example.shared.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        String detail = "The requested resource: ";
        //detail = detail.concat(ex.getMessage());
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                detail.concat(HttpStatus.NOT_FOUND.name())
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String detail = "An unexpected error occurred  ";
        //detail = detail.concat(ex.getMessage());
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                detail.concat(HttpStatus.NOT_FOUND.name())
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String detail = "The HTTP verb is not supported for this endpoint. ";
        //detail = detail.concat(ex.getMessage());
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.METHOD_NOT_ALLOWED.value(),
                detail.concat(HttpStatus.METHOD_NOT_ALLOWED.name()));
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String detail = "The media type is not supported. ";
        //detail = detail.concat(ex.getMessage());
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                detail.concat(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name()));
        return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        String detail = "The media type is not acceptable. ";
        //detail = detail.concat(ex.getMessage());
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE.value(),
                detail.concat(HttpStatus.NOT_ACCEPTABLE.name()));
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

}
