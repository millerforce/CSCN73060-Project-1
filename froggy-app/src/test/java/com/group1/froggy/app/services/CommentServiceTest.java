package com.group1.froggy.app.services;

import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.comment.CommentRepository;
import com.group1.froggy.jpa.post.comment.like.CommentLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void createComment_Success(){

    }

    @Test
    void editComment_Success(){

    }

    @Test
    void deleteComment_Success(){

    }

    @Test
    void likeComment_Success(){

    }

    @Test
    void getCommentLikes_Success(){

    }



}
