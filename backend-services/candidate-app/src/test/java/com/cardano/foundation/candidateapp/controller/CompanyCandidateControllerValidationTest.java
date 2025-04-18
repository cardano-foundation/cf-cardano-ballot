package com.cardano.foundation.candidateapp.controller;

import com.cardano.foundation.candidateapp.service.CompanyCandidateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyCandidateController.class)
@Import(CompanyCandidateControllerValidationTest.MockConfig.class)
class CompanyCandidateControllerValidationTest {

    @Resource MockMvc mockMvc;
    @Resource ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CompanyCandidateService service() {
            return Mockito.mock(CompanyCandidateService.class);
        }
    }

    @Test
    void shouldReturn400ForMissingRegistrationNumber() throws Exception {
        String json = """
        {
          "candidate": {
            "candidateType": "company",
            "name": "Test Co",
            "email": "contact@test.co",
            "country": "Poland",
            "publicContact": "test handle"
          },
          "keyContactPerson": "CEO"
        }
        """;

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.registrationNumber").value("must not be blank"));
    }
}
