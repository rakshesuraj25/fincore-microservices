package com.exception;

public class UserAlreadyExist extends RuntimeException {

    public UserAlreadyExist(String msg) {
        super(msg);
    }
}