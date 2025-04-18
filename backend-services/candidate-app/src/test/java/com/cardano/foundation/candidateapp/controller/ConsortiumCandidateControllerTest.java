package com.cardano.foundation.candidateapp.controller;

import com.cardano.foundation.candidateapp.dto.*;
import com.cardano.foundation.candidateapp.model.CandidateType;
import com.cardano.foundation.candidateapp.service.ConsortiumCandidateService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsortiumCandidateController.class)
@Import(ConsortiumCandidateControllerTest.MockConfig.class)
class ConsortiumCandidateControllerTest {

    @Resource MockMvc mockMvc;
    @Resource ConsortiumCandidateService service;
    @Resource ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ConsortiumCandidateService service() {
            return Mockito.mock(ConsortiumCandidateService.class);
        }
    }

    @Test
    void shouldCreateConsortium() throws Exception {
        CandidateResponseDto candidateDto = CandidateResponseDto.builder()
                .name("DAO United")
                .email("dao@united.org")
                .country("Country")
                .publicContact("dao@united.org")
                .candidateType(CandidateType.consortium)
                .build();

        ConsortiumMemberResponseDto member = ConsortiumMemberResponseDto.builder()
                .name("Alice")
                .country("Sweden")
                .build();
        ConsortiumMemberResponseDto member2 = ConsortiumMemberResponseDto.builder()
                .name("Alice2")
                .country("Sweden2")
                .build();

        ConsortiumCandidateResponseDto input = ConsortiumCandidateResponseDto.builder()
                .candidate(candidateDto)
                .members(List.of(member, member2))
                .build();

        when(service.create(any())).thenReturn(input);

        mockMvc.perform(post("/api/consortia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidate.name").value("DAO United"))
                .andExpect(jsonPath("$.members[0].name").value("Alice"))
                .andExpect(jsonPath("$.members[1].name").value("Alice2"));
    }
}
