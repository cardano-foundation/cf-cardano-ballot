package com.cardano.foundation.candidateapp.controller;

import com.cardano.foundation.candidateapp.service.VerifyCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate/verification")
@RequiredArgsConstructor
public class VerifyCandidateController {

    private final VerifyCandidateService service;

    @GetMapping("/verify/{id}")
    public ResponseEntity<Void> verify(@PathVariable Long id) {
        service.verify(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unverify/{id}")
    public ResponseEntity<Void> unverify(@PathVariable Long id) {
        service.unverify(id);
        return ResponseEntity.ok().build();
    }
}
