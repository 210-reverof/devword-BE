package com.burnbunny.devword.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserResponse {
    private int statusCode;
    private String resMessage;
    private Object data;

}
