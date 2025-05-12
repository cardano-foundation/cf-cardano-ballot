package com.cardano.foundation.candidateapp.repository.comment;

import com.cardano.foundation.candidateapp.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByCandidateId(Long candidateId);
}
