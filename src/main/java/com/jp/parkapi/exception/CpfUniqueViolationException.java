package com.jp.parkapi.exception;

public class CpfUniqueViolationException extends RuntimeException {

    public CpfUniqueViolationException(String msg) {
        super(msg);
    }
}
