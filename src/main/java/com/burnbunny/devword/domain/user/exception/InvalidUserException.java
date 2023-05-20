package com.burnbunny.devword.domain.user.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(final String message) {
        super(message);
    }
    public InvalidUserException() {
        this("잘못된 회원 정보입니다.");
    }
}
