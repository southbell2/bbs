package com.bbs.backend.exception;

public class UserAlreadyExistsEx extends RuntimeException{
    public UserAlreadyExistsEx(String message) {
        super(message);
    }
}
