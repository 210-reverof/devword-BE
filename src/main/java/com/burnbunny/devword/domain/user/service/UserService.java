package com.burnbunny.devword.domain.user.service;

import com.burnbunny.devword.domain.user.Role;
import com.burnbunny.devword.domain.user.User;
import com.burnbunny.devword.domain.user.dto.UserSignUpDto;
import com.burnbunny.devword.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.existsByEmail(userSignUpDto.getEmail())) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByNickname(userSignUpDto.getNickname())) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }

        // TODO: 2023/05/13 pwcheck과 비교 추가, 응답 수정하기

        User newUser = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .role(Role.USER)
                .build();

        newUser.passwordEncode(passwordEncoder);
        userRepository.save(newUser);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}