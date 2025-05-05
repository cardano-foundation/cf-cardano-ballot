package com.cardano.foundation.candidateapp.controller;

import com.cardano.foundation.candidateapp.dto.CandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CandidateResponseDto;
import com.cardano.foundation.candidateapp.dto.CompanyCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CompanyCandidateResponseDto;
import com.cardano.foundation.candidateapp.model.CandidateType;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyCandidateController.class)
@Import(CompanyCandidateControllerTest.MockConfig.class)
class CompanyCandidateControllerTest {

    @Resource MockMvc mockMvc;
    @Resource CompanyCandidateService service;
    @Resource ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CompanyCandidateService mockCompanyService() {
            return Mockito.mock(CompanyCandidateService.class);
        }
    }

    @Test
    void shouldCreateCompanyCandidate() throws Exception {
        CandidateRequestDto candidate = CandidateRequestDto.builder()
                .name("Acme Corp")
                .email("info@acme.com")
                .country("Country")
                .publicContact("info@acme.com")
                .walletAddress("walletAddress")
                .build();
        CompanyCandidateRequestDto request = CompanyCandidateRequestDto.builder()
                .candidate(candidate)
                .keyContactPerson("keyContact")
                .registrationNumber("REG-42")
                .build();
        CandidateResponseDto base = CandidateResponseDto.builder()
                .name("Acme Corp")
                .email("info@acme.com")
                .country("Country")
                .publicContact("info@acme.com")
                .walletAddress("walletAddress")
                .candidateType(CandidateType.company)
                .build();

        CompanyCandidateResponseDto dto = new CompanyCandidateResponseDto(base, "REG-42", "keyContact");

        when(service.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber").value("REG-42"))
                .andExpect(jsonPath("$.candidate.name").value("Acme Corp"))
                .andExpect(jsonPath("$.candidate.publicContact").value("info@acme.com"))
                .andExpect(jsonPath("$.candidate.country").value("Country"))
                .andExpect(jsonPath("$.candidate.walletAddress").value("walletAddress"));
    }
}
