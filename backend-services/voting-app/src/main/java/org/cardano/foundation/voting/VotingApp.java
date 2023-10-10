package org.cardano.foundation.voting;

import io.micrometer.core.aop.TimedAspect;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
@EnableJpaRepositories("org.cardano.foundation.voting.repository")
@EntityScan(basePackages = "org.cardano.foundation.voting.domain.entity")
@ComponentScan(basePackages = {
		"org.cardano.foundation.voting.repository",
		"org.cardano.foundation.voting.service",
		"org.cardano.foundation.voting.resource",
		"org.cardano.foundation.voting.config",
		"org.cardano.foundation.voting.client"
})
@EnableTransactionManagement
@Slf4j
@ImportRuntimeHints(VotingApp.Hints.class)
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
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

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        @SneakyThrows
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerMethod(TimedAspect.class.getMethod("timedMethod", ProceedingJoinPoint.class), ExecutableMode.INVOKE);
            hints.resources().registerResource(new ClassPathResource("db/migration/h2/V0__voting_app_init.sql"));
            hints.resources().registerResource(new ClassPathResource("db/migration/postgresql/V0__voting_app_init.sql"));
        }
    }

}
