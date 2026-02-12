package com.group1.froggy.jpa.post.comment.like;

import com.group1.froggy.jpa.post.comment.CommentJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeJpa, CommentLikeId> {
    long countByComment(CommentJpa comment);
    void deleteAllByComment(CommentJpa comment);
}
