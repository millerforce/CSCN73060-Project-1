package com.group1.froggy.app.services;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.post.Post;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.AccountRepository;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostJpa;
import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.comment.CommentRepository;
import com.group1.froggy.jpa.post.like.PostLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    }

    @Test
    void deletePost_Success(){

    }

    @Test
    void likePost_Success(){

    }

    @Test
    void getPostLikes_Success(){

    }


}
