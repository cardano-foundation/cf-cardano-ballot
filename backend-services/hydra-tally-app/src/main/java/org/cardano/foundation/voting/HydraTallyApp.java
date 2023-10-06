package org.cardano.foundation.voting;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.repository.LocalVoteRepository;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@ComponentScan(basePackages = {
		"org.cardano.foundation.voting.service",
		"org.cardano.foundation.voting.config"
})
@EnableCommand
@CommandScan(basePackages = { "org.cardano.foundation.voting.shell" })
@Slf4j
public class HydraTallyApp {

	@Value("${votes.path}")
	private String votesPath;

    public static void main(String[] args) {
		SpringApplication.run(HydraTallyApp.class, args);
	}

	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster =
				new SimpleApplicationEventMulticaster();

		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());

		return eventMulticaster;
	}

	@Bean
	@SneakyThrows
	public VoteRepository voteRepository(ResourceLoader resourceLoader) {
		var r = resourceLoader.getResource(votesPath);

		return new LocalVoteRepository(r.getFile().getPath());
	}

}
