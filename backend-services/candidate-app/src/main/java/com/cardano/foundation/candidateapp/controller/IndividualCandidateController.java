package com.cardano.foundation.candidateapp.controller;

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

    @PostMapping
    public ResponseEntity<IndividualCandidateResponseDto> create(@RequestBody @Valid IndividualCandidateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndividualCandidateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<IndividualCandidateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndividualCandidateResponseDto> update(@PathVariable Long id, @RequestBody @Valid IndividualCandidateRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
