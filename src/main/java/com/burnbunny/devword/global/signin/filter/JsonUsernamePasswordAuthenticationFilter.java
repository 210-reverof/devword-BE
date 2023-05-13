package com.burnbunny.devword.global.signin.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_SIGNIN_REQUEST_URL = "/user/sign-in";
    public static final String HTTP_METHOD = "POST";
    public static final String CONTENT_TYPE = "application/json";
    public static final String USERNAME_KEY = "email";
    public static final String PASSWORD_KEY = "password";
    public static final AntPathRequestMatcher DEFAULT_SIGNIN_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(DEFAULT_SIGNIN_REQUEST_URL, HTTP_METHOD);
    public final ObjectMapper objectMapper;
    private boolean postOnly = true;

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_SIGNIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals(HTTP_METHOD)) {
            log.error("로그인 요청의 HTTP_MOTHOD가 잘못되었습니다. : {}", request.getMethod());
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (request.getContentType()==null || !request.getContentType().equals(CONTENT_TYPE)) {
            log.error("로그인 요청의 ContentType이 잘못되었습니다. : {}", request.getContentType());
            throw new AuthenticationServiceException("Authentication Cont-Type not supported: " + request.getContentType());
        }

        ServletInputStream inputStream = request.getInputStream();
        String msgBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        Map<String, String> signinDataMap = objectMapper.readValue(msgBody, new TypeReference<>() {});

        String email = signinDataMap.get(USERNAME_KEY);
        String password = signinDataMap.get(PASSWORD_KEY);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        return getAuthenticationManager().authenticate(authRequest);
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
