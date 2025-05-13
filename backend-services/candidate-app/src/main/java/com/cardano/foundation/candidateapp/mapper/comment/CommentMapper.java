package com.cardano.foundation.candidateapp.mapper.comment;

import com.cardano.foundation.candidateapp.dto.comment.CommentRequestDto;
import com.cardano.foundation.candidateapp.dto.comment.CommentResponseDto;
import com.cardano.foundation.candidateapp.model.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment toEntity(CommentRequestDto dto);

    default CommentResponseDto toDto(Comment comment) {
        var parentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
        return new CommentResponseDto(
                comment.getId(),
                comment.getCandidate().getId(),
                comment.getCommentText(),
                comment.getAuthorWalletAddress(),
                parentId,
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
