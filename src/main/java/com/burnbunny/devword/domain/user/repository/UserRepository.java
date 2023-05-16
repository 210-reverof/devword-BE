package com.burnbunny.devword.domain.user.repository;

import com.burnbunny.devword.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByRefreshToken_Token(String refreshToken);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}