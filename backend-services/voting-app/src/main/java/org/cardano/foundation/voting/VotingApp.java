package org.cardano.foundation.voting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class })
@EnableJpaRepositories("org.cardano.foundation.voting.repository")
@EntityScan(basePackages = "org.cardano.foundation.voting.domain.entity")
@ComponentScan(basePackages = {
		"org.cardano.foundation.voting.repository",
		"org.cardano.foundation.voting.service",
		"org.cardano.foundation.voting.resource",
		"org.cardano.foundation.voting.config",
		"org.cardano.foundation.voting.client",
		"org.cardano.foundation.voting.jobs"
})
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class VotingApp {

	public static void main(String[] args) {
		SpringApplication.run(VotingApp.class, args);
	}

	@Bean
	public CommandLineRunner onStart() {
		return (args) -> {
			log.info("Voting App started.");
		};
	}

}
