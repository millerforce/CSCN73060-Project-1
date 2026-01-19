package com.group1.froggy.app.services;

import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.post.PostUpload;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostJpa;
import com.group1.froggy.jpa.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class PostService {

    private final AuthorizationService authorizationService;
    private final PostRepository postRepository;

    public List<Post> getPosts(String cookie, Integer lastNPosts) {
        if (!authorizationService.isValidSession(cookie)) {
            throw new InvalidCredentialsException("Invalid session cookie");
        }

        return postRepository.findLatestPosts(lastNPosts)
            .stream()
            .map(PostJpa::toPost)
            .toList();
    }

    public Post createPost(String cookie, PostUpload postUpload) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = PostJpa.create(sessionJpa.getAccount(), postUpload);

        postJpa = postRepository.save(postJpa);

        return postJpa.toPost();
    }

    public Post editPost(String cookie, UUID postId, PostUpload postUpload) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!postJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the post");
        }

        postJpa.setContent(postUpload.content());

        return postRepository.save(postJpa).toPost();
    }

    public void deletePost(String cookie, UUID postId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!postJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the post");
        }

        postRepository.delete(postJpa);
    }

    public Post likePost(String cookie, UUID postId) {
        return null;
    }

}
