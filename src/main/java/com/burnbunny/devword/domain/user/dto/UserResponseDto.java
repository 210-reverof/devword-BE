package com.burnbunny.devword.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
public class UserResponseDto {
    private int statusCode;
    private String resMessage;
    private Object data;

}
