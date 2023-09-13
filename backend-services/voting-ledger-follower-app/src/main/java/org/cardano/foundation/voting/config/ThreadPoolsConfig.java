package org.cardano.foundation.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolsConfig {

    @Bean(name = "singleThreadExecutor")
    public Executor threadPoolTaskExecutor() {
        return Executors.newSingleThreadExecutor();
    }

}
