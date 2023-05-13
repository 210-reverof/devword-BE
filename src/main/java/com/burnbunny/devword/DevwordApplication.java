package com.burnbunny.devword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DevwordApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevwordApplication.class, args);
	}

}
