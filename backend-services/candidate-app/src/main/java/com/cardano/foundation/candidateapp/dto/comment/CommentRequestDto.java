package com.cardano.foundation.candidateapp.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotNull
    private Long candidateId;

    @NotBlank
    private String commentText;

    @NotBlank
    private String authorWalletAddress;

    private Long parentCommentId;
}
