package com.burnbunny.devword.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.burnbunny.devword.domain.user.User;
import com.burnbunny.devword.domain.user.repository.UserRepository;
import com.burnbunny.devword.global.jwt.RefreshTokenEntity;
import com.burnbunny.devword.global.jwt.repository.RefreshTokenRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretkey}")
    private String secretkey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;


    public String createAccessToken(String email) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretkey));
    }
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretkey));
    }

    public void addRefreshTokenToHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }
    public void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    public void sendAccessAndRefreshTokenInResponse(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        addAccessTokenToHeader(response, accessToken);
        addRefreshTokenToHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    public void sendAccessTokenInResponse(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        addAccessTokenToHeader(response, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    public Optional<String> extractRefreshTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }
    public Optional<String> extractAccessTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Optional<String> extractEmailFromAccessToken(String accessToken) {
        try {
            log.info("extractEmail 호출");
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretkey)).build().verify(accessToken);
            return Optional.ofNullable(decodedJWT.getClaim(EMAIL_CLAIM).asString());
        } catch (Exception e) {
            log.error("엑세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    public void updateRefreshToken(String email, String refreshToken) {
        if (!userRepository.existsByEmail(email)) {
            new Exception("해당 이메일과 일치하는 회원이 없습니다.");
            return;
        }
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRedisRepository.save(refreshTokenEntity);
    }

    public User findUserByRefreshToken(String refreshToken) throws ServiceException {
        RefreshTokenEntity refreshTokenEntity =
                refreshTokenRedisRepository.findById(refreshToken)
                        .orElseThrow(() -> new ServiceException("일치하는 refresh token이 없습니다."));

        String email = refreshTokenEntity.getEmail();

        return userRepository.findByEmail(email).orElseThrow(() -> new ServiceException("이메일과 일치하는 유저가 없습니다."));
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretkey)).build().verify(token);
            log.info("유효한 토큰입니다.");
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
