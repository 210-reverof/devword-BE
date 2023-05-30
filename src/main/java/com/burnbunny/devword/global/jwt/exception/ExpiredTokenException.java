package com.burnbunny.devword.global.jwt.exception;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(final String message) {
        super(message);
    }

    public ExpiredTokenException() {
        this("만료된 토큰입니다.");
    }
}
