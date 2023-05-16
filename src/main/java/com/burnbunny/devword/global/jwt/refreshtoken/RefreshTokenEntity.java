package com.burnbunny.devword.global.jwt.refreshtoken;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh", timeToLive = 36000)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenEntity {
    @Id
    private String email;

    @Indexed
    private String refreshToken;
}
