package com.cardano.foundation.candidateapp.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI candidateAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Candidate Application API")
                        .description("API documentation for managing individual, company, and consortium candidates.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("BinarApps Team")
                                .url("https://binarapps.com")
                        ));
    }
}
