package com.cardano.foundation.candidateapp.controller.candidate;

import com.cardano.foundation.candidateapp.dto.CompanyCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.CompanyCandidateResponseDto;
import com.cardano.foundation.candidateapp.service.CompanyCandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyCandidateController {

    private final CompanyCandidateService service;
    private final static boolean IS_DRAFT = false;

    @PostMapping
    public ResponseEntity<CompanyCandidateResponseDto> create(@RequestBody @Valid CompanyCandidateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto, IS_DRAFT));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyCandidateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id, IS_DRAFT));
    }

    @GetMapping("/wallet/{walletAddress}")
    public ResponseEntity<List<CompanyCandidateResponseDto>> getByWalletAddress(@PathVariable String walletAddress) {
        return ResponseEntity.ok(service.getAllByWalletAddress(walletAddress, IS_DRAFT));
    }

    @GetMapping()
    public ResponseEntity<List<CompanyCandidateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll(IS_DRAFT));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyCandidateResponseDto> update(@PathVariable Long id, @RequestBody @Valid CompanyCandidateRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto, IS_DRAFT));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id, IS_DRAFT);
        return ResponseEntity.noContent().build();
    }
}
