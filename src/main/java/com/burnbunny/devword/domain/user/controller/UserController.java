package com.burnbunny.devword.domain.user.controller;

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
    public String signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return "회원가입 성공";
    }

    @GetMapping("/check/{email}")
    public boolean checkEmail(@PathVariable String email) {
        return userService.isEmailAvailable(email);
    }

    @GetMapping("/token-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }
}
