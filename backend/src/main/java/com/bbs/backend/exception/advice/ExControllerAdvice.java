package com.bbs.backend.exception.advice;

import com.bbs.backend.exception.ExceptionResponse;
import com.bbs.backend.exception.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.bbs.backend.controller")
public class ExControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> postNotFoundEx(PostNotFoundException e, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), e.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
