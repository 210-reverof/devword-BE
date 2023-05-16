package com.burnbunny.devword.global.jwt.filter;

import com.burnbunny.devword.domain.user.User;
import com.burnbunny.devword.domain.user.repository.UserRepository;
import com.burnbunny.devword.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/user/sign-in";
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.extractRefreshTokenFromRequest(request).orElse(null);

        if (refreshToken == null)
            checkAccessTokenAndAuthentication(request, response, filterChain);
        else
            checkRefreshAndReissueTokens(response, refreshToken);

    }

    public void checkRefreshAndReissueTokens(HttpServletResponse response, String refreshToken) {
        boolean tokenValid = jwtService.isTokenValid(refreshToken);

        if (!tokenValid){
            // TODO: 2023/05/15 refresh token 만료. 재로그인 요청 추가.
            log.info("refresh token 만료. 재로그인 필요.");
            return;
        }

        User user = userRepository.findByRefreshToken_Token(refreshToken).orElseThrow(() -> new ServiceException("잘못된 refresh token입니다."));

        String reissuedRefreshToken = reissueRefreshToken(user);
        String reissuedAccessToken = jwtService.createAccessToken(user.getEmail());
        jwtService.sendAccessAndRefreshTokenInResponse(response, reissuedAccessToken, reissuedRefreshToken);
    }

    private String reissueRefreshToken(User user) {
        String reissuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reissuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reissuedRefreshToken;
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationProcessingFilter.checkAccessTokenAndAuthentication 호출");

        jwtService.extractAccessTokenFromRequest(request)
                .filter(jwtService::isTokenValid)
                .flatMap(jwtService::extractEmailFromToken)
                .flatMap(userRepository::findByEmail)
                .ifPresent(this::saveAuthentication);

        filterChain.doFilter(request,response);
    }
    public void saveAuthentication(User user) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
