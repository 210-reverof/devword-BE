package com.burnbunny.devword.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {
    // TODO: 2023/05/13 pwcheck 추가하기

    private String email;
    private String password;
    private String nickname;

}
