package com.burnbunny.devword.domain.user;

import com.burnbunny.devword.global.jwt.RefreshTokenEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshTokenEntity refreshToken;

    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String newPassword) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public void updateRefreshToken(String reissuedRefreshToken) {
        if (this.refreshToken == null) {
            this.refreshToken = new RefreshTokenEntity(reissuedRefreshToken, this);
        } else {
            this.refreshToken.setToken(reissuedRefreshToken);
        }
    }
}
