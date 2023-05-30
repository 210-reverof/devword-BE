package com.burnbunny.devword.global.jwt.exception;

import com.burnbunny.devword.domain.user.exception.InvalidUserException;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(final String message) {
        super(message);
    }

    public InvalidTokenException() {
        this("유효하지 않은 토큰입니다.");
    }
}
