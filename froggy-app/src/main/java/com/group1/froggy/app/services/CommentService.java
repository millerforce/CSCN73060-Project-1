package com.group1.froggy.app.services;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.comment.Comment;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.jpa.account.AccountJpa;
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

    /**
     * Retrieve comments for a post for an authorized session.
     *
     * @param cookie raw Cookie header value used to validate the session
     * @param postId id of the post to fetch comments for
     * @return list of Comment DTOs including like counts and whether the current user liked each comment
     * @throws com.group1.froggy.app.exceptions.InvalidCredentialsException when the session is invalid or missing
     */
    public List<Comment> getCommentsByPost(String cookie, UUID postId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        return commentRepository.findCommentJpaByPostId(postId).stream()
            .map(commentJpa -> toCommentWithLikes(sessionJpa.getAccount(), commentJpa))
            .toList();
    }

    /**
     * Create a new comment on a post authored by the account associated with the provided session cookie.
     *
     * @param cookie raw Cookie header value used to validate the session
     * @param postId id of the post to comment on
     * @param content content payload for the new comment
     * @return created Comment DTO including metadata and like counts
     * @throws EntityNotFoundException when the post cannot be found
     * @throws com.group1.froggy.app.exceptions.InvalidCredentialsException when the session is invalid or missing
     */
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

        return toCommentWithLikes(sessionJpa.getAccount(), commentJpa);
    }

    /**
     * Edit an existing comment. Only the author may edit their comment.
     *
     * @param cookie raw Cookie header value used to validate the session
     * @param commentId id of the comment to edit
     * @param content new content for the comment
     * @return updated Comment DTO
     * @throws EntityNotFoundException when the comment cannot be found
     * @throws com.group1.froggy.app.exceptions.InvalidCredentialsException when the session is invalid or missing
     */
    public Comment editComment(String cookie, UUID commentId, Content content) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        CommentJpa commentJpa = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentJpa.setContent(content.content());
        commentJpa.setUpdatedAt(LocalDateTime.now());

        return toCommentWithLikes(sessionJpa.getAccount(), commentRepository.save(commentJpa));
    }

    /**
     * Delete an existing comment authored by the requesting account.
     *
     * @param cookie raw Cookie header value used to validate the session
     * @param commentId id of the comment to delete
     * @throws EntityNotFoundException when the comment cannot be found
     * @throws IllegalActionException when the requesting account is not the author
     */
    public void deleteComment(String cookie, UUID commentId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        CommentJpa commentJpa = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!commentJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the comment");
        }
        commentLikeRepository.deleteAllByComment(commentJpa);
        commentRepository.delete(commentJpa);
    }

    /**
     * Add a like from the current user to the specified comment.
     *
     * @param cookie raw Cookie header value used to validate the session
     * @param commentId id of the comment to like
     * @return Comment DTO reflecting the updated like count and whether the current user liked it
     * @throws EntityNotFoundException when the comment cannot be found
     */
    public Comment likeComment(String cookie, UUID commentId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        CommentJpa commentJpa = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        commentLikeRepository.save(CommentLikeJpa.create(commentJpa, sessionJpa.getAccount()));

        return toCommentWithLikes(sessionJpa.getAccount(), commentJpa);
    }

    private Comment toCommentWithLikes(AccountJpa accountJpa, CommentJpa commentJpa) {
        long likes = commentLikeRepository.countByComment(commentJpa);
        boolean likedByCurrentUser = commentLikeRepository.existsById(CommentLikeJpa.create(commentJpa, accountJpa).getId());
        return new Comment(
            commentJpa.getId(),
            commentJpa.getPost().getId(),
            commentJpa.getAccount().toAccount(),
            commentJpa.getContent(),
            commentJpa.getCreatedAt(),
            commentJpa.getUpdatedAt(),
            likes,
            likedByCurrentUser
        );
    }
}
