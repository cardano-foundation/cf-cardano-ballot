package com.cardano.foundation.candidateapp.controller.comment;

import com.cardano.foundation.candidateapp.dto.comment.CommentRequestDto;
import com.cardano.foundation.candidateapp.dto.comment.CommentResponseDto;
import com.cardano.foundation.candidateapp.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody @Valid CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.createComment(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CommentResponseDto>> getAllByCandidateId(@PathVariable Long candidateId) {
        return ResponseEntity.ok(commentService.getAllByCandidateId(candidateId));
    }
}
