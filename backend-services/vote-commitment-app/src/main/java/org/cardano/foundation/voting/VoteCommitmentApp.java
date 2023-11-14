package org.cardano.foundation.voting;

import com.bloxbean.cardano.client.backend.blockfrost.service.http.*;
import io.micrometer.core.aop.TimedAspect;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.aot.hint.ExecutableMode.INVOKE;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
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
@ImportRuntimeHints(VoteCommitmentApp.Hints.class)
@EnableAsync
public class VoteCommitmentApp {

	public static void main(String[] args) {
		SpringApplication.run(VoteCommitmentApp.class, args);
	}

	@Bean
	public CommandLineRunner onStart() {
		return (args) -> {
			log.info("Vote Commitment App started.");
		};
	}

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        @SneakyThrows
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerMethod(TimedAspect.class.getMethod("timedMethod", ProceedingJoinPoint.class), INVOKE);
			hints.proxies().registerJdkProxy(AddressesApi.class);
			hints.proxies().registerJdkProxy(TransactionApi.class);
			hints.proxies().registerJdkProxy(AccountApi.class);
			hints.proxies().registerJdkProxy(BlockApi.class);
			hints.proxies().registerJdkProxy(EpochApi.class);
			hints.proxies().registerJdkProxy(MetadataApi.class);
			hints.proxies().registerJdkProxy(AssetsApi.class);
			hints.proxies().registerJdkProxy(CardanoLedgerApi.class);
			hints.proxies().registerJdkProxy(ScriptApi.class);
        }
    }

}
