package org.cardano.foundation.voting;

import io.micrometer.core.aop.TimedAspect;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

import static org.springframework.aot.hint.ExecutableMode.INVOKE;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages = {
        "org.cardano.foundation.voting.client",
        "org.cardano.foundation.voting.service",
        "org.cardano.foundation.voting.resource",
        "org.cardano.foundation.voting.config",
})
@Slf4j
@ImportRuntimeHints(VotingVerificationApp.Hints.class)
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

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        @SneakyThrows
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerMethod(TimedAspect.class.getMethod("timedMethod", ProceedingJoinPoint.class), INVOKE);
        }
    }

}
