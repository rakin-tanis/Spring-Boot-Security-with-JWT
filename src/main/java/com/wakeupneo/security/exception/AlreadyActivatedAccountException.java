package com.wakeupneo.security.exception;

public class AlreadyActivatedAccountException extends RuntimeException {

    private static final long serialVersionUID = 1781514417283638870L;

    public AlreadyActivatedAccountException(String message) {
        super(message);
    }
}
