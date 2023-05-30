package com.burnbunny.devword.global.exception;

import com.burnbunny.devword.domain.user.dto.response.UserResponse;
import com.burnbunny.devword.domain.user.exception.InvalidUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionController {
    private static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<UserResponse> handleInvalidUserException(InvalidUserException exception) {

        logger.error("유저 정보 예외발생", exception.getCause());
        exception.printStackTrace();

        String message = exception.getMessage();
        UserResponse userResponse = new UserResponse(message, false);

        return ResponseEntity.badRequest().body(userResponse);
    }
}
