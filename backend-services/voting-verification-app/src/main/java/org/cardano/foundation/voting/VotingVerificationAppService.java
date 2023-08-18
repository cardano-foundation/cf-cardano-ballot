package org.cardano.foundation.voting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@Slf4j
public class VotingVerificationAppService {

	public static void main(String[] args) {
		SpringApplication.run(VotingVerificationAppService.class, args);
	}

	@Bean
	public CommandLineRunner onStart() {
		return (args) -> {
			log.info("Voting Verification App Service started.");
		};
	}

}
