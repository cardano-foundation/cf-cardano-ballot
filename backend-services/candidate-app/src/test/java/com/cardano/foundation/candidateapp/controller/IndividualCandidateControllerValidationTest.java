package com.cardano.foundation.candidateapp.controller;

import com.cardano.foundation.candidateapp.service.IndividualCandidateService;
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

@WebMvcTest(controllers = IndividualCandidateController.class)
@Import(IndividualCandidateControllerValidationTest.MockConfig.class)
class IndividualCandidateControllerValidationTest {

    @Resource MockMvc mockMvc;
    @Resource ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public IndividualCandidateService service() {
            return Mockito.mock(IndividualCandidateService.class);
        }
    }

    @Test
    void shouldReturn400ForMissingRequiredFields() throws Exception {
        // No name, email, country, publicContact
        String invalidJson = """
        {
          "candidate": {
            "candidateType": "individual"
          }
        }
        """;

        mockMvc.perform(post("/api/individuals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.['candidate.name']").value("must not be blank"))
                .andExpect(jsonPath("$.fieldErrors.['candidate.email']").value("must not be blank"));
    }

    @Test
    void shouldReturn400ForInvalidEmail() throws Exception {
        String json = """
        {
          "candidate": {
            "candidateType": "individual",
            "name": "Invalid Email",
            "email": "not-an-email",
            "country": "Nowhere",
            "publicContact": "t.me/test"
          }
        }
        """;

        mockMvc.perform(post("/api/individuals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.['candidate.email']").value("must be a well-formed email address"));
    }
}
