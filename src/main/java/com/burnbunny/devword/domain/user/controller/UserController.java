package com.burnbunny.devword.domain.user.controller;

import com.burnbunny.devword.domain.user.dto.UserResponseDto;
import com.burnbunny.devword.domain.user.dto.UserSignUpDto;
import com.burnbunny.devword.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public UserResponseDto signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        Long userId = userService.signUp(userSignUpDto);
        String resMessage = (userId!=null) ? "회원 가입 완료" : "에러 메시지";

        return new UserResponseDto(200, resMessage, userId);
    }

    @GetMapping("/check/{email}")
    public UserResponseDto checkEmail(@PathVariable String email) {
        boolean emailAvailable = userService.isEmailAvailable(email);
        String resMessage = emailAvailable ? "사용 가능한 이메일" : "사용 불가능한 이메일";

        return new UserResponseDto(200, resMessage, emailAvailable);
    }

    @GetMapping("/token-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }
}
