package com.group1.froggy.app.services;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.post.PostStats;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.AccountRepository;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostJpa;
import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.comment.CommentRepository;
import com.group1.froggy.jpa.post.like.PostLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_Success(){

        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(accountUpload.password())
                .createdAt(LocalDateTime.now())
                .build();

        SessionJpa sessionJpa = SessionJpa.create("token", accountJpa);
        Content content = new Content("passed around willy stick in class today #fire emoji");

        when(authorizationService.validateSession(any())).thenReturn(sessionJpa);

        when(postRepository.save(any(PostJpa.class))).thenAnswer(invocation -> {
            PostJpa saved = invocation.getArgument(0);
            return PostJpa.builder()
                    .id(UUID.randomUUID())
                    .account(saved.getAccount())
                    .content(saved.getContent())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();
        });

        Post post = postService.createPost(sessionJpa.getToken(), content);

        assertNotNull(post);
        assertEquals("passed around willy stick in class today #fire emoji", post.content());
        verify(postRepository).save(any(PostJpa.class));
    }

    @Test
    void editPost_Success(){
        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(accountUpload.password())
                .createdAt(LocalDateTime.now())
                .build();

        SessionJpa sessionJpa = SessionJpa.create("token", accountJpa);

        PostJpa existing = PostJpa.builder()
                .id(UUID.randomUUID())
                .account(accountJpa)
                .content("old")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(postRepository.findById(any())).thenReturn(Optional.of(existing));
        when(postRepository.save(existing)).thenReturn(existing);


        Post post = postService.editPost("session=valid", any(), new Content("new"));

        assertNotNull(post);
        assertEquals("new", post.content());
        verify(postRepository).save(existing);
    }
    @Test
    void editPost_NotOwner() {
        AccountJpa owner = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username("owner")
                .hashedPassword("123")
                .createdAt(LocalDateTime.now())
                .build();

        AccountJpa otherUser = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username("other")
                .hashedPassword("123")
                .createdAt(LocalDateTime.now())
                .build();

        SessionJpa sessionJpa = SessionJpa.create("token", otherUser);

        PostJpa existing = PostJpa.builder()
                .id(UUID.randomUUID())
                .account(owner)
                .content("post")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(postRepository.findById(any())).thenReturn(Optional.of(existing));

        assertThrows(IllegalActionException.class, () ->
                postService.editPost("session=valid", any(), new Content("new")));

        verify(postRepository, never()).save(any(PostJpa.class));
    }

    @Test
    void deletePost_Success() {
        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(accountUpload.password())
                .createdAt(LocalDateTime.now())
                .build();

        SessionJpa sessionJpa = SessionJpa.create("token", accountJpa);

        PostJpa existing = PostJpa.builder()
                .id(UUID.randomUUID())
                .account(accountJpa)
                .content("post")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(postRepository.findById(any())).thenReturn(Optional.of(existing));

        postService.deletePost("session=valid", any());

        verify(postLikeRepository).deleteAllByPost(existing);
        verify(postRepository).delete(existing);
    }

    @Test
    void likePost_Success() {
        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(accountUpload.password())
                .createdAt(LocalDateTime.now())
                .build();

        SessionJpa sessionJpa = SessionJpa.create("token", accountJpa);

        PostJpa existing = PostJpa.builder()
                .id(UUID.randomUUID())
                .account(accountJpa)
                .content("post")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        when(authorizationService.validateSession("session=valid")).thenReturn(sessionJpa);
        when(postRepository.findById(any())).thenReturn(Optional.of(existing));
        when(postLikeRepository.countByPost(existing)).thenReturn(4L);
        when(commentRepository.countByPostId(any())).thenReturn(1L);

        Post post = postService.likePost("session=valid", any());

        assertNotNull(post);
        assertEquals(4L, post.numberOfLikes());
        assertEquals(1L, post.numberOfComments());
        verify(postLikeRepository).save(any());
    }


    @Test
    void getPostStats_Success() {
        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(accountUpload.password())
                .createdAt(LocalDateTime.now())
                .build();

        PostJpa postJpa = PostJpa.builder()
                .id(UUID.randomUUID())
                .account(accountJpa)
                .content("post")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        when(authorizationService.isValidSession("session=valid")).thenReturn(true);
        when(postRepository.findById(any())).thenReturn(Optional.of(postJpa));
        when(postLikeRepository.countByPost(postJpa)).thenReturn(2L);
        when(commentRepository.countByPostId(postJpa.getId())).thenReturn(2L);

        PostStats stats = postService.getPostStats("session=valid", any());

        assertEquals(65, stats.trendingScore());
    }

    @Test
    void getPostStats_PostNotFound() {
        UUID postId = UUID.randomUUID();

        when(authorizationService.isValidSession("session=valid")).thenReturn(true);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                postService.getPostStats("session=valid", postId));
    }
}
