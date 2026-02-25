package com.group1.froggy.app.services;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.comment.Comment;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostJpa;
import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.comment.CommentJpa;
import com.group1.froggy.jpa.post.comment.CommentRepository;
import com.group1.froggy.jpa.post.comment.like.CommentLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private  CommentService commentService;


    @Test
    void getCommentsByPost_Success() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();
        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        AccountJpa author1 = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("author1")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        PostJpa post1 = PostJpa.builder()
            .id(UUID.randomUUID())
            .account(author1)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        CommentJpa first = CommentJpa.builder()
            .id(UUID.randomUUID())
            .post(post1)
            .account(author1)
            .content("c1")
            .createdAt(LocalDateTime.now().minusHours(2))
            .updatedAt(LocalDateTime.now().minusHours(2))
            .build();

        AccountJpa author2 = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("author2")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        PostJpa post2 = PostJpa.builder()
            .id(UUID.randomUUID())
            .account(author2)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        CommentJpa second = CommentJpa.builder()
            .id(UUID.randomUUID())
            .post(post2)
            .account(author2)
            .content("c2")
            .createdAt(LocalDateTime.now().minusHours(2))
            .updatedAt(LocalDateTime.now().minusHours(2))
            .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findCommentJpaByPostId(any())).thenReturn(List.of(first, second));
        when(commentLikeRepository.countByComment(any(CommentJpa.class))).thenReturn(3L);
        when(commentLikeRepository.existsById(any())).thenReturn(false).thenReturn(true);

        List<Comment> comments = commentService.getCommentsByPost("session=valid", any());

        assertEquals(2, comments.size());
        assertEquals("c1", comments.get(0).content());
        assertEquals("c2", comments.get(1).content());
        assertFalse(comments.get(0).likedByCurrentUser());
        assertTrue(comments.get(1).likedByCurrentUser());
    }

    @Test
    void createComment_Success() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        AccountJpa owner = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("owner")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        UUID postId = UUID.randomUUID();
        PostJpa postJpa = PostJpa.builder()
            .id(postId)
            .account(owner)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.getReferenceById(postId)).thenReturn(postJpa);
        when(commentRepository.save(any(CommentJpa.class))).thenAnswer(invocation -> {
            CommentJpa input = invocation.getArgument(0);

            return CommentJpa.builder()
                .id(UUID.randomUUID())
                .post(input.getPost())
                .account(input.getAccount())
                .content(input.getContent())
                .createdAt(input.getCreatedAt())
                .updatedAt(input.getUpdatedAt())
                .build();
        });
        when(commentLikeRepository.countByComment(any(CommentJpa.class))).thenReturn(0L);
        when(commentLikeRepository.existsById(any())).thenReturn(false);

        Comment created = commentService.createComment("session=valid", postId, new Content("hello"));

        assertNotNull(created);
        assertEquals(postId, created.postId());
        assertEquals("hello", created.content());
        verify(commentRepository).save(any(CommentJpa.class));
    }

    @Test
    void createComment_PostNotFound_Throws() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        UUID postId = UUID.randomUUID();
        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            commentService.createComment("session=valid", postId, new Content("hello")));

        verify(commentRepository, never()).save(any(CommentJpa.class));
    }

    @Test
    void editComment_Success() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        AccountJpa owner = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("owner")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        PostJpa postJpa = PostJpa.builder()
            .id(postId)
            .account(owner)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        CommentJpa existing = CommentJpa.builder()
            .id(commentId)
            .post(postJpa)
            .account(currentUser)
            .content("old")
            .createdAt(LocalDateTime.now().minusHours(2))
            .updatedAt(LocalDateTime.now().minusHours(2))
            .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepository.save(existing)).thenReturn(existing);
        when(commentLikeRepository.countByComment(existing)).thenReturn(2L);
        when(commentLikeRepository.existsById(any())).thenReturn(true);

        Comment updated = commentService.editComment("session=valid", commentId, new Content("new"));

        assertNotNull(updated);
        assertEquals("new", updated.content());
        verify(commentRepository).save(existing);
    }

    @Test
    void editComment_NotFound_Throws() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        UUID commentId = UUID.randomUUID();
        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            commentService.editComment("session=valid", commentId, new Content("new")));
    }

    @Test
    void deleteComment_Success() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        AccountJpa owner = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("owner")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        PostJpa postJpa = PostJpa.builder()
            .id(postId)
            .account(owner)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        CommentJpa existing = CommentJpa.builder()
            .id(commentId)
            .post(postJpa)
            .account(currentUser)
            .content("comment")
            .createdAt(LocalDateTime.now().minusHours(1))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        commentService.deleteComment("session=valid", commentId);

        verify(commentRepository).delete(existing);
    }

    @Test
    void deleteComment_NotAuthor_Throws() {
        AccountJpa owner = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("owner")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        AccountJpa other = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("other")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(other.getId(), "token"))
            .account(other)
            .createdAt(LocalDateTime.now())
            .build();

        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        AccountJpa postOwner = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("postOwner")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        PostJpa postJpa = PostJpa.builder()
            .id(postId)
            .account(postOwner)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        CommentJpa existing = CommentJpa.builder()
            .id(commentId)
            .post(postJpa)
            .account(owner)
            .content("comment")
            .createdAt(LocalDateTime.now().minusHours(1))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalActionException.class, () ->
            commentService.deleteComment("session=valid", commentId));

        verify(commentRepository, never()).delete(any(CommentJpa.class));
    }

    @Test
    void likeComment_Success() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        UUID commentId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        AccountJpa postOwner = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("owner")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        PostJpa postJpa = PostJpa.builder()
            .id(postId)
            .account(postOwner)
            .content("post")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        AccountJpa commentAuthor = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("commentAuthor")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        CommentJpa existing = CommentJpa.builder()
            .id(commentId)
            .post(postJpa)
            .account(commentAuthor)
            .content("comment")
            .createdAt(LocalDateTime.now().minusHours(1))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentLikeRepository.countByComment(existing)).thenReturn(4L);
        when(commentLikeRepository.existsById(any())).thenReturn(true);

        Comment liked = commentService.likeComment("session=valid", commentId);

        assertEquals(4L, liked.numberOfLikes());
        assertTrue(liked.likedByCurrentUser());
        verify(commentLikeRepository).save(any());
    }

    @Test
    void likeComment_NotFound_Throws() {
        AccountJpa currentUser = AccountJpa.builder()
            .id(UUID.randomUUID())
            .username("current")
            .hashedPassword("hashed")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        SessionJpa sessionJpa = SessionJpa.builder()
            .id(SessionJpa.createId(currentUser.getId(), "token"))
            .account(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

        UUID commentId = UUID.randomUUID();
        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            commentService.likeComment("session=valid", commentId));
    }

}
