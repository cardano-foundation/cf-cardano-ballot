package org.cardano.foundation.voting.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

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

}
