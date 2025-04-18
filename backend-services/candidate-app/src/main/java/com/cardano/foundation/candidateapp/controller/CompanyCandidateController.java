package com.cardano.foundation.candidateapp.controller;

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

    @PostMapping
    public ResponseEntity<CompanyCandidateResponseDto> create(@RequestBody @Valid CompanyCandidateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyCandidateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<CompanyCandidateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyCandidateResponseDto> update(@PathVariable Long id, @RequestBody @Valid CompanyCandidateRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
