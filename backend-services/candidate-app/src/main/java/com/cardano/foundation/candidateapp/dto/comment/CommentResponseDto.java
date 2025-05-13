package com.cardano.foundation.candidateapp.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long candidateId;
    private String commentText;
    private String authorWalletAddress;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
