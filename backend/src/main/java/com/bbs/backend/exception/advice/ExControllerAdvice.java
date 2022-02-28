package com.bbs.backend.exception.advice;

import com.bbs.backend.dto.ExceptionDTO;
import com.bbs.backend.exception.PostNotFoundException;
import com.bbs.backend.exception.UserAlreadyExistsEx;
import com.bbs.backend.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.bbs.backend.controller")
public class ExControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> postNotFoundEx(PostNotFoundException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> userNotFoundEx(UserNotFoundException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> userAlreadyEx(UserAlreadyExistsEx e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }
}
