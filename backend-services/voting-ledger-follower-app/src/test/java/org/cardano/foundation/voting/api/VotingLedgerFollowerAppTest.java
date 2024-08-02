package org.cardano.foundation.voting.api;

import com.bloxbean.cardano.yaci.store.api.blocks.service.BlockService;
import com.bloxbean.cardano.yaci.store.api.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
@Slf4j
public class VotingLedgerFollowerAppTest {

    @Bean
    @Primary
    public BlockService blockService() {
        log.info("Mocking Yaci's BlockService...");
        return mock(BlockService.class);
    }

    @Bean
    @Primary
    public TransactionService transactionService() {
       log.info("Mocking Yaci's TransactionService...");
       return mock(TransactionService.class);
    }

}
