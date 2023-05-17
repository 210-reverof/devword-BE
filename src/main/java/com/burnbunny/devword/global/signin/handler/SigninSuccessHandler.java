package com.burnbunny.devword.global.signin.handler;

import com.burnbunny.devword.domain.user.dto.UserResponseDto;
import com.burnbunny.devword.global.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigninSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = extractUserEmail(authentication);
        String accessToken = issueAndSendTokens(response, email);

        log.info("로그인 성공.");
        log.info("로그인 데이터: ");
        log.info("email: {}", email);

        String jsonResult = objectMapper.writeValueAsString(new UserResponseDto(200, "로그인 완료", accessToken));
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonResult);
    }

    private String issueAndSendTokens(HttpServletResponse response, String email) {
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(email, refreshToken);
        jwtService.sendAccessAndRefreshTokenInResponse(response, accessToken, refreshToken);
        return accessToken;

    }

    private String extractUserEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}
