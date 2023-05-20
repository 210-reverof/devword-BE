package com.burnbunny.devword.global.config;

import com.burnbunny.devword.domain.user.repository.UserRepository;
import com.burnbunny.devword.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.burnbunny.devword.global.jwt.filter.JwtExceptionFilter;
import com.burnbunny.devword.global.jwt.service.JwtService;
import com.burnbunny.devword.global.signin.filter.JsonUsernamePasswordAuthenticationFilter;
import com.burnbunny.devword.global.signin.handler.SigninFailureHandler;
import com.burnbunny.devword.global.signin.handler.SigninSuccessHandler;
import com.burnbunny.devword.global.signin.service.SigninService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final SigninService signinService;

    private final SigninSuccessHandler signinSuccessHandler;
    private final SigninFailureHandler signinFailureHandler;

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeHttpRequests()
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll()
                .requestMatchers("/user/sign-up", "user/check/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterAfter(jwtExceptionFilter(), LogoutFilter.class);
        http.addFilterAfter(jwtAuthenticationProcessingFilter(), JwtExceptionFilter.class);
        http.addFilterAfter(jsonUsernamePasswordAuthenticationFilter(), JwtAuthenticationProcessingFilter.class);

        return http.build();
    }

    @Bean
    JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository);

    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(signinService);
        return new ProviderManager(provider);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter signinFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        signinFilter.setAuthenticationManager(authenticationManager());
        signinFilter.setAuthenticationSuccessHandler(signinSuccessHandler);
        signinFilter.setAuthenticationFailureHandler(signinFailureHandler);
        return signinFilter;
    }
}
