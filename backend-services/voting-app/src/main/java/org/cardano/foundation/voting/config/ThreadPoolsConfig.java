package org.cardano.foundation.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolsConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setQueueCapacity(100);

        threadPoolTaskExecutor.setCorePoolSize(2);

        threadPoolTaskExecutor.setMaxPoolSize(5);

        return threadPoolTaskExecutor;
    }

    @Bean(name = "asyncExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setQueueCapacity(100);

        threadPoolTaskExecutor.setCorePoolSize(2);

        threadPoolTaskExecutor.setMaxPoolSize(5);

        return threadPoolTaskExecutor;
    }

}
