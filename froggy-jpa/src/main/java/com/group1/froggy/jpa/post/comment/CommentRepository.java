package com.group1.froggy.jpa.post.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentJpa, UUID> {
    List<CommentJpa> findCommentJpaByPostId(UUID postId);
    long countByPostId(UUID postId);
}
