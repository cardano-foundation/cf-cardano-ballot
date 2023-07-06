package org.cardano.foundation.voting.jobs;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProcessRecentMetadataJob implements Runnable {

    @Autowired
    private MetadataService metadataService;

    @Override
    @Scheduled(fixedDelayString = "PT5M", initialDelayString = "PT0M")
    public void run() {
        log.info("Starting ProcessRecentMetadataJob...");
        metadataService.processRecentMetadataEvents();
        log.info("Finished ProcessRecentMetadataJob...");
    }

}
