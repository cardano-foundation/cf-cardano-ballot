package org.cardano.foundation.voting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@EnableCommand
@ComponentScan(basePackages = { "org.cardano.foundation.voting.service", "org.cardano.foundation.voting.config", "org.cardano.foundation.voting.shell" })
@Slf4j
public class VotingAdminApp {

    public static void main(String[] args) {
		SpringApplication.run(VotingAdminApp.class, args);
	}

	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster =
				new SimpleApplicationEventMulticaster();

		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());

		return eventMulticaster;
	}

}
