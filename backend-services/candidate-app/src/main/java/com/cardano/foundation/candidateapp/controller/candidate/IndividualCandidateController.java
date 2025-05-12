package com.cardano.foundation.candidateapp.controller.candidate;

import com.cardano.foundation.candidateapp.dto.IndividualCandidateRequestDto;
import com.cardano.foundation.candidateapp.dto.IndividualCandidateResponseDto;
import com.cardano.foundation.candidateapp.service.IndividualCandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/individuals")
@RequiredArgsConstructor
public class IndividualCandidateController {

    private final IndividualCandidateService service;
    private final static boolean IS_DRAFT = false;

    @PostMapping
    public ResponseEntity<IndividualCandidateResponseDto> create(@RequestBody @Valid IndividualCandidateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto, IS_DRAFT));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndividualCandidateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id, IS_DRAFT));
    }

    @GetMapping("/wallet/{walletAddress}")
    public ResponseEntity<List<IndividualCandidateResponseDto>> getByWalletAddress(@PathVariable String walletAddress) {
        return ResponseEntity.ok(service.getAllByWalletAddress(walletAddress, IS_DRAFT));
    }

    @GetMapping()
    public ResponseEntity<List<IndividualCandidateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll(IS_DRAFT));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndividualCandidateResponseDto> update(@PathVariable Long id, @RequestBody @Valid IndividualCandidateRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto, IS_DRAFT));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id, IS_DRAFT);
        return ResponseEntity.noContent().build();
    }
}
