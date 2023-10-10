package org.cardano.foundation.voting;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@ComponentScan(basePackages = {
		"org.cardano.foundation.voting.service",
		"org.cardano.foundation.voting.config"
})
@EnableCommand
@CommandScan(basePackages = { "org.cardano.foundation.voting.shell" })
@Slf4j
public class HydraTallyApp implements PromptProvider {

	@Autowired
	private CardanoNetwork network;

	@Value("${hydra.operator.name}")
	private String actor;

	@Autowired
	private HydraReactiveClient hydraReactiveClient;

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

	@Override
	public AttributedString getPrompt() {
		var prompt = String.format("%s:%s:%s>>", actor, hydraReactiveClient.getHydraState().toString().toUpperCase(), network);

		return new AttributedString(prompt);
	}

}
