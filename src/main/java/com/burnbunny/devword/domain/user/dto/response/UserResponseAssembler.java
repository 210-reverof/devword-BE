package com.burnbunny.devword.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseAssembler {
    public static UserResponse userResponse(int statusCode, String resMessage, Object obj) {
        return new UserResponse(statusCode, resMessage, obj);
    }
}
