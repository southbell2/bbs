package com.bbs.backend.exception.advice;

import com.bbs.backend.dto.ExceptionDTO;
import com.bbs.backend.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.bbs.backend.controller")
public class ExControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> notFoundEx(NotFoundException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> badRequestEx(BadRequestException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> methodArgsEx(MethodArgumentNotValidException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> unAuthorizedEx(UnauthorizedException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> forbiddenEx(ForbiddenException e, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exceptionDTO, HttpStatus.FORBIDDEN);
    }
}
