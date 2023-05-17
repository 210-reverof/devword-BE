package com.burnbunny.devword.domain.user.controller;

import com.burnbunny.devword.domain.user.dto.response.UserResponse;
import com.burnbunny.devword.domain.user.dto.request.UserSignUpDto;
import com.burnbunny.devword.domain.user.service.UserService;
import com.burnbunny.devword.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public UserResponse signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        Long userId = userService.signUp(userSignUpDto);
        String resMessage = (userId!=null) ? "회원 가입 완료" : "에러 메시지";

        return new UserResponse(200, resMessage, userId);
    }

    @GetMapping("/check/{email}")
    public UserResponse checkEmail(@PathVariable String email) {
        boolean emailAvailable = userService.isEmailAvailable(email);
        String resMessage = emailAvailable ? "사용 가능한 이메일" : "사용 불가능한 이메일";

        return new UserResponse(200, resMessage, emailAvailable);
    }

    @GetMapping("/token-test")
    public UserResponse jwtTest(HttpServletRequest request) {
        String token = jwtService.extractAccessTokenFromRequest(request).orElse(null);
        Optional<String> email = jwtService.extractEmailFromAccessToken(token);
        String resMessage = email.isPresent() ? "토큰으로 이메일 조회 완료" : "이메일 조회 실패";

        return new UserResponse(200, resMessage, email.orElse(null));
    }
}
