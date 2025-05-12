package com.cardano.foundation.candidateapp.controller.candidate;

import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.ConsortiumCandidateResponseDto;
import com.cardano.foundation.candidateapp.service.ConsortiumCandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consortia")
@RequiredArgsConstructor
public class ConsortiumCandidateController {

    private final ConsortiumCandidateService service;
    private final static boolean IS_DRAFT = false;

    @PostMapping
    public ResponseEntity<ConsortiumCandidateResponseDto> create(@RequestBody @Valid ConsortiumCandidateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto, IS_DRAFT));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsortiumCandidateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id, IS_DRAFT));
    }

    @GetMapping("/wallet/{walletAddress}")
    public ResponseEntity<List<ConsortiumCandidateResponseDto>> getByWalletAddress(@PathVariable String walletAddress) {
        return ResponseEntity.ok(service.getAllByWalletAddress(walletAddress, IS_DRAFT));
    }

    @GetMapping()
    public ResponseEntity<List<ConsortiumCandidateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll(IS_DRAFT));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsortiumCandidateResponseDto> update(@PathVariable Long id, @RequestBody @Valid ConsortiumCandidateRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto, IS_DRAFT));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id, IS_DRAFT);
        return ResponseEntity.noContent().build();
    }
}
