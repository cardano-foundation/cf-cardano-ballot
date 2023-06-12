package org.cardano.foundation.voting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.domain.entity.Proposal;
import org.cardano.foundation.voting.service.ReferenceDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.net.http.HttpClient;
import java.time.Duration;
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
@Import(SecurityProblemSupport.class)
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
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(60))
                .build();
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
    public CommandLineRunner onStart(ReferenceDataService referenceDataService) {
        return (args) -> {
            log.info("CF Voting App initialisation...");

            Optional<Event> maybeVoltaireEvent = referenceDataService.findEventByName("Voltaire_Pre_Ratification");
            if (maybeVoltaireEvent.isPresent()) {
                log.info("There is already event: {}", maybeVoltaireEvent.orElseThrow());

                log.info("CF Voting App initialisation completed.");
                return;
            }

            log.info("Creating event along with proposals...");

            Event event = new Event();
            //event.setId(UUID.randomUUID().toString());
            event.setId("5abcb6a2-f9a9-4617-b9ce-10b9dd290354");
            event.setName("Voltaire_Pre_Ratification");
            event.setTeam("CF Team");
            event.setStartSlot(415);
            event.setEndSlot(425);
            event.setSnapshotEpoch(410);
            event.setSnapshotEpochType(EPOCH_END);

            event.setDescription("Pre-Ratification of the Voltaire era");

            Category preRatificationCategory = new Category();
            //preRatificationCategory.setId(UUID.randomUUID().toString());
            preRatificationCategory.setId("e969729d-ab08-4ca3-a17d-13f3a8b8c0ab");
            preRatificationCategory.setName("Pre-Ratification");
            preRatificationCategory.setDescription("Pre-Ratification for CIP-1694");
            preRatificationCategory.setPresentationName("Pre-Ratification");

            Proposal yesProposal = new Proposal();
            //yesProposal.setId(UUID.randomUUID().toString());
            yesProposal.setId("ffb9fd11-b82b-4766-bcd5-b8e7b760624a");
            yesProposal.setName("YES");
            yesProposal.setPresentationName("Yes");
            yesProposal.setCategory(preRatificationCategory);

            Proposal noProposal = new Proposal();
            //noProposal.setId(UUID.randomUUID().toString());
            noProposal.setId("ffb9fd11-b82b-4766-bcd5-b8e7b760624b");
            noProposal.setName("NO");
            noProposal.setPresentationName("No");
            noProposal.setCategory(preRatificationCategory);

            Proposal abstainProposal = new Proposal();
            //abstainProposal.setId(UUID.randomUUID().toString());
            abstainProposal.setId("ffb9fd11-b82b-4766-bcd5-b8e7b760624c");
            abstainProposal.setName("ABSTAIN");
            abstainProposal.setPresentationName("Abstain");
            abstainProposal.setCategory(preRatificationCategory);

            preRatificationCategory.setEvent(event);
            preRatificationCategory.setProposals(List.of(yesProposal, noProposal, abstainProposal));
            event.setCategories(List.of(preRatificationCategory));

            referenceDataService.storeEvent(event);

            log.info("CF Voting App initialisation completed.");
        };
    }

}
