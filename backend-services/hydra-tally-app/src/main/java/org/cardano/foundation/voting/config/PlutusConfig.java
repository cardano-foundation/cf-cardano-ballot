package org.cardano.foundation.voting.config;

import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.CreateVoteBatchRedeemerConverter;
import org.cardano.foundation.voting.domain.ReduceVoteBatchRedeemerConverter;
import org.cardano.foundation.voting.domain.VoteDatumConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlutusConfig {

    @Bean
    public CategoryResultsDatumConverter categoryResultsDatumConverter() {
        return new CategoryResultsDatumConverter();
    }

    @Bean
    public VoteDatumConverter voteDatumConverter() {
        return new VoteDatumConverter();
    }

    @Bean
    public ReduceVoteBatchRedeemerConverter reduceVoteBatchRedeemerConverter() {
        return new ReduceVoteBatchRedeemerConverter();
    }

    @Bean
    public CreateVoteBatchRedeemerConverter createVoteBatchRedeemerConverter() {
        return new CreateVoteBatchRedeemerConverter();
    }

}
