package com.group1.froggy.app.services;

import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.post.Content;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostJpa;
import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.like.PostLikeJpa;
import com.group1.froggy.jpa.post.like.PostLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final AuthorizationService authorizationService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public List<Post> getPosts(String cookie, Integer lastNPosts, Integer offset) {
        if (!authorizationService.isValidSession(cookie)) {
            throw new InvalidCredentialsException("Invalid session cookie");
        }

        int size = (lastNPosts == null || lastNPosts <= 0) ? 10 : lastNPosts;
        int skip = (offset == null || offset < 0) ? 0 : offset;

        int page = skip / size;
        int indexInPage = skip % size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<PostJpa> pageContent = postRepository.findLatestPosts(pageable);

        List<PostJpa> combined = new ArrayList<>(pageContent);

        if (indexInPage != 0) {
            Pageable nextPageable = PageRequest.of(page + 1, size, Sort.by("createdAt").descending());
            List<PostJpa> nextPageContent = postRepository.findLatestPosts(nextPageable);
            combined.addAll(nextPageContent);
        }

        return combined.stream()
            .skip(indexInPage)
            .limit(size)
            .map(this::toPostWithLikes)
            .toList();
    }

    public Post createPost(String cookie, Content content) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = PostJpa.create(sessionJpa.getAccount(), content);

        postJpa = postRepository.save(postJpa);

        return toPostWithLikes(postJpa);
    }

    public Post editPost(String cookie, UUID postId, Content content) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!postJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the post");
        }

        postJpa.setContent(content.content());

        return toPostWithLikes(postRepository.save(postJpa));
    }

    public void deletePost(String cookie, UUID postId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!postJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the post");
        }

        postLikeRepository.deleteAllByPost(postJpa);
        postRepository.delete(postJpa);
    }

    public Post likePost(String cookie, UUID postId) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));


        postLikeRepository.save(PostLikeJpa.create(postJpa, sessionJpa.getAccount()));

        return toPostWithLikes(postJpa);
    }

    private Post toPostWithLikes(PostJpa postJpa) {
        long likes = postLikeRepository.countByPost(postJpa);
        return new Post(
            postJpa.getId(),
            postJpa.getAccount().toAccount(),
            postJpa.getContent(),
            likes,
            postJpa.getCreatedAt(),
            postJpa.getUpdatedAt()
        );
    }

}
