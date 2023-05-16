package com.burnbunny.devword.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserResponseDto {
    private int statusCode;
    private String resMessage;
    private Object data;

}
