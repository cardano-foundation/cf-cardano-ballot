package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.AccountService;
import io.blockfrost.sdk.api.BlockService;
import io.blockfrost.sdk.api.MetadataService;
import io.blockfrost.sdk.api.TransactionService;
import io.blockfrost.sdk.impl.AccountServiceImpl;
import io.blockfrost.sdk.impl.BlockServiceImpl;
import io.blockfrost.sdk.impl.MetadataServiceImpl;
import io.blockfrost.sdk.impl.TransactionServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractBlockfrostService {

    @Value("${blockfrost.api.key}")
    private String blockfrostProjectId;

    @Value("${blockfrost.url}")
    private String blockfrostUrl;

    protected MetadataService metadataService;

    protected BlockService blockService;

    protected TransactionService transactionService;

    protected AccountService accountService;

    @PostConstruct
    public void init() {
        this.metadataService = new MetadataServiceImpl(blockfrostUrl, blockfrostProjectId);
        this.blockService = new BlockServiceImpl(blockfrostUrl, blockfrostProjectId);
        this.transactionService = new TransactionServiceImpl(blockfrostUrl, blockfrostProjectId);
        this.accountService = new AccountServiceImpl(blockfrostUrl, blockfrostProjectId);
    }

}
