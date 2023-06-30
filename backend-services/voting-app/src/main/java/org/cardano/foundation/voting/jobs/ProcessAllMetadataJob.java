package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProcessAllMetadataJob implements Runnable {

    @Autowired
    private MetadataService metadataService;

    @Override
    public void run() {
        log.info("Starting ProcessAllMetadataJob...");
        metadataService.processAllMetadataEvents();
        log.info("Finished ProcessAllMetadataJob.");
    }

}
