package org.cardano.foundation.voting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaRepositories("org.cardano.foundation.voting.repository")
@EntityScan(basePackages = "org.cardano.foundation.voting.domain.entity")
@ComponentScan(basePackages = {
		"org.cardano.foundation.voting.repository",
		"org.cardano.foundation.voting.service",
		"org.cardano.foundation.voting.resource",
		"org.cardano.foundation.voting.handlers",
		"org.cardano.foundation.voting.config",
		"org.cardano.foundation.voting.jobs"
})
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class VotingLedgerFollowerApp {

	public static void main(String[] args) {
		SpringApplication.run(VotingLedgerFollowerApp.class, args);
	}

	@Bean
	public CommandLineRunner onStart() {
		return (args) -> {
			log.info("Voting Ledger Follower App started.");
		};
	}

}
