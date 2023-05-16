package com.burnbunny.devword.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.burnbunny.devword.domain")
public class JpaConfig {
}
