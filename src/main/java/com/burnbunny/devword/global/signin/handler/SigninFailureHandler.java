package com.burnbunny.devword.global.signin.handler;

import com.burnbunny.devword.domain.user.dto.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SigninFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String jsonResult = objectMapper.writeValueAsString(new UserResponseDto(200, "로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.", null));
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonResult);
        log.info("로그인 실패: {}", exception.getMessage());


    }
}
