package com.group1.froggy.app.services;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.comment.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    public List<Comment> getCommentsByPost(UUID postId) {
        return null;
    }

    public Comment createComment(String cookie, UUID postId, Content content) {
        return null;
    }

    public Comment editComment(String cookie, UUID commentId, Content content) {
        return null;
    }

    public void deleteComment(String cookie, UUID commentId) {

    }

    public Comment likeComment(String cookie, UUID commentId) {
        return null;
    }


}
