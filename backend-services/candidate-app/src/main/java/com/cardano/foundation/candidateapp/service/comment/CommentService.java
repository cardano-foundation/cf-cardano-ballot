package com.cardano.foundation.candidateapp.service.comment;

import com.cardano.foundation.candidateapp.dto.comment.CommentRequestDto;
import com.cardano.foundation.candidateapp.dto.comment.CommentResponseDto;
import com.cardano.foundation.candidateapp.exception.ResourceNotFoundException;
import com.cardano.foundation.candidateapp.mapper.comment.CommentMapper;
import com.cardano.foundation.candidateapp.model.candidate.Candidate;
import com.cardano.foundation.candidateapp.model.comment.Comment;
import com.cardano.foundation.candidateapp.repository.candidate.CandidateRepository;
import com.cardano.foundation.candidateapp.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CandidateRepository candidateRepository;
    private final CommentRepository repository;
    private final CommentMapper mapper;

    public CommentResponseDto createComment(CommentRequestDto dto) {
        Candidate candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        if (candidate.isDraft()) {
            throw new IllegalArgumentException("Can't add comments to drafts");
        }

        Comment comment = mapper.toEntity(dto);
        Comment parentComment = null;

        if (dto.getParentCommentId() != null) {
            parentComment = repository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            if (!parentComment.getCandidate().getId().equals(dto.getCandidateId())) {
                throw new ResourceNotFoundException("Parent comment is from another candidate");
            }
        }

        comment.setParentComment(parentComment);
        comment.setCandidate(candidate);
        Comment saved = repository.save(comment);
        return mapper.toDto(saved);
    }

    public CommentResponseDto getById(Long id) {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return mapper.toDto(comment);
    }

    public List<CommentResponseDto> getAllByCandidateId(Long candidateId) {
        candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        List<Comment> comments = repository.findAllByCandidateId(candidateId);
        return comments.stream()
                .map(mapper::toDto)
                .toList();
    }
}
