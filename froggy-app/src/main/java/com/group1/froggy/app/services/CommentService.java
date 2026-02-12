package com.group1.froggy.app.services;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.comment.Comment;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.comment.CommentJpa;
import com.group1.froggy.jpa.post.comment.CommentRepository;
import com.group1.froggy.jpa.post.comment.like.CommentLikeJpa;
import com.group1.froggy.jpa.post.comment.like.CommentLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final AuthorizationService authorizationService;
    private final CommentLikeRepository commentLikeRepository;

    public List<Comment> getCommentsByPost(String cookie, UUID postId) {
        if (!authorizationService.isValidSession(cookie)) {
            throw new InvalidCredentialsException("Invalid session cookie");
        }

        return commentRepository.findCommentJpaByPostId(postId).stream()
            .map(this::toCommentWithLikes)
            .toList();
    }

    public Comment createComment(String cookie, UUID postId, Content content) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found");
        }

        CommentJpa commentJpa = CommentJpa.create(
            postRepository.getReferenceById(postId),
            sessionJpa.getAccount(),
            content
        );

        commentJpa = commentRepository.save(commentJpa);

        return toCommentWithLikes(commentJpa);
    }

    public Comment editComment(String cookie, UUID commentId, Content content) {
        if (!authorizationService.isValidSession(cookie)) {
            throw new InvalidCredentialsException("Invalid session cookie");
        }

        CommentJpa commentJpa = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentJpa.setContent(content.content());
        commentJpa.setUpdatedAt(LocalDateTime.now());

        return toCommentWithLikes(commentRepository.save(commentJpa));
    }

    public void deleteComment(String cookie, UUID commentId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        CommentJpa commentJpa = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!commentJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the comment");
        }

        commentRepository.delete(commentJpa);
    }

    public Comment likeComment(String cookie, UUID commentId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        CommentJpa commentJpa = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentLikeRepository.save(CommentLikeJpa.create(commentJpa, sessionJpa.getAccount()));

        return toCommentWithLikes(commentJpa);
    }

    private Comment toCommentWithLikes(CommentJpa commentJpa) {
        long likes = commentLikeRepository.countByComment(commentJpa);
        return new Comment(
            commentJpa.getId(),
            commentJpa.getPost().getId(),
            commentJpa.getAccount().toAccount(),
            commentJpa.getContent(),
            commentJpa.getCreatedAt(),
            commentJpa.getUpdatedAt(),
            likes
        );
    }
}
