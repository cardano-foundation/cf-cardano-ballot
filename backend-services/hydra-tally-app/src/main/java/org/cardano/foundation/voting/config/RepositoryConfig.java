package org.cardano.foundation.voting.config;

import lombok.SneakyThrows;
import org.cardano.foundation.voting.repository.LocalVoteRepository;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class RepositoryConfig {

    @Value("${votes.path}")
    private String votesPath;

    @Bean
    @SneakyThrows
    public VoteRepository voteRepository(ResourceLoader resourceLoader) {
        var r = resourceLoader.getResource(votesPath);

        return new LocalVoteRepository(r.getFile().getPath());
    }

}
