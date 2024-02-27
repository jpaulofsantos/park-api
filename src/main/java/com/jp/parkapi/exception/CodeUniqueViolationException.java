package com.jp.parkapi.exception;

public class CodeUniqueViolationException extends RuntimeException{

    public CodeUniqueViolationException(String msg) {
        super(msg);
    }
}
