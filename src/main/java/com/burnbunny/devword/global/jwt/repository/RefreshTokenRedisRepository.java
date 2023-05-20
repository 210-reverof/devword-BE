package com.burnbunny.devword.global.jwt.repository;

import com.burnbunny.devword.global.jwt.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRedisRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findById(String refreshToken);
}
