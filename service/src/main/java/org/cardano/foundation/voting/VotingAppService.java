package org.cardano.foundation.voting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Network;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.service.ReferenceDataCreator;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.cardano.foundation.voting.domain.SnapshotEpochType.EPOCH_END;

@SpringBootApplication
@EnableJpaRepositories("org.cardano.foundation.voting.repository")
@EntityScan(basePackages = "org.cardano.foundation.voting.domain.entity")
@ComponentScan(basePackages = { "org.cardano.foundation.voting.service", "org.cardano.foundation.voting.resource" })
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
//@Import(SecurityProblemSupport.class)
@Slf4j
public class VotingAppService {

    public static void main(String[] args) {
		SpringApplication.run(VotingAppService.class, args);
	}

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        log.info("Registered jackson modules:");
        objectMapper.getRegisteredModuleIds().forEach(moduleId -> {
            log.info("Module: {}", moduleId);
        });

        return objectMapper;
    }

    @Bean
    public Network network(@Value("${cardano.network:main}") String networkName) {
        var network = Network.fromName(networkName).orElseThrow(() -> new RuntimeException("Invalid network name: " + networkName));

        log.info("Configured backend network:{}", network);

        return network;
    }

//    @Bean("canonical_object_mapper")
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper(new CanonicalFactory()) {
//
//            // Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jsonSchemaConverter' defined in class path resource [org/springframework/data/rest/webmvc/config/RepositoryRestMvcConfiguration.class]: Failed to instantiate [org.springframework.data.rest.webmvc.json.PersistentEntityToJsonSchemaConverter]: Factory method 'jsonSchemaConverter' threw exception with message: Failed copy(): io.setl.json.jackson.CanonicalFactory (version: 2.14.2) does not override copy(); it has to
//            // workaround: https://stackoverflow.com/questions/60608345/spring-boot-hateoas-and-custom-jacksonobjectmapper
//
//            @Override
//            public ObjectMapper copy() {
//                return this;
//            }
//
//        };
//    }

    @Bean
    public CommandLineRunner onStart(ReferenceDataCreator referenceDataCreator) {

        return (args) -> {
            log.info("CF Voting App initialisation...");

            referenceDataCreator.createReferenceData();

            log.info("CF Voting App initialisation completed.");
        };
    }

}
