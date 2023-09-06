package org.cardano.foundation.voting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages = {
		"org.cardano.foundation.voting.client",
		"org.cardano.foundation.voting.service",
		"org.cardano.foundation.voting.resource",
		"org.cardano.foundation.voting.config",
})
@Slf4j
public class VotingVerificationApp {

	public static void main(String[] args) {
		SpringApplication.run(VotingVerificationApp.class, args);
	}

	@Bean
	public CommandLineRunner onStart() {
		return (args) -> {
			log.info("Voting Verification App started.");
		};
	}

}
