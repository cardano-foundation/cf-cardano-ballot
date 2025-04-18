package com.cardano.foundation.candidateapp.controller;

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

    @PostMapping
    public ResponseEntity<ConsortiumCandidateResponseDto> create(@RequestBody @Valid ConsortiumCandidateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsortiumCandidateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ConsortiumCandidateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsortiumCandidateResponseDto> update(@PathVariable Long id, @RequestBody @Valid ConsortiumCandidateRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
