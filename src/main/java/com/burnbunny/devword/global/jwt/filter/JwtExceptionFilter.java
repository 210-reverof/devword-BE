package com.burnbunny.devword.global.jwt.filter;

import com.burnbunny.devword.domain.user.dto.response.UserResponse;
import com.burnbunny.devword.domain.user.exception.InvalidUserException;
import com.burnbunny.devword.global.jwt.exception.ExpiredTokenException;
import com.burnbunny.devword.global.jwt.exception.InvalidTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public JwtExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredTokenException | InvalidUserException e) {
            sendExceptionResponse(response, e, HttpServletResponse.SC_BAD_REQUEST);

        } catch (InvalidTokenException e) {
            sendExceptionResponse(response, e, HttpServletResponse.SC_UNAUTHORIZED);

        }
    }

    private void sendExceptionResponse(HttpServletResponse response, Exception e, int statusCode) throws IOException {
        String jsonResult = objectMapper.writeValueAsString(new UserResponse(e.getMessage(), null));
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonResult);

        log.error(e.getMessage());
        e.printStackTrace();
    }
}
