package com.group1.froggy.app.services;

import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.Content;
import com.group1.froggy.api.post.PostStats;
import com.group1.froggy.app.exceptions.IllegalActionException;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.post.PostJpa;
import com.group1.froggy.jpa.post.PostRepository;
import com.group1.froggy.jpa.post.comment.CommentRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final AuthorizationService authorizationService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    public List<Post> getPosts(String cookie, Integer lastNPosts, Integer offset) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

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
            .map(postJpa -> toPostWithLikes(sessionJpa.getAccount(), postJpa))
            .toList();
    }

    public Post createPost(String cookie, Content content) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = PostJpa.create(sessionJpa.getAccount(), content);

        postJpa = postRepository.save(postJpa);

        return toPostWithLikes(sessionJpa.getAccount(), postJpa);
    }

    public Post editPost(String cookie, UUID postId, Content content) {
        SessionJpa sessionJpa = authorizationService.validateSession(cookie);

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!postJpa.getAccount().getId().equals(sessionJpa.getAccount().getId())) {
            throw new IllegalActionException("Only the author can delete the post");
        }

        postJpa.setContent(content.content());
        postJpa.setUpdatedAt(LocalDateTime.now());

        return toPostWithLikes(sessionJpa.getAccount(), postRepository.save(postJpa));
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

        return toPostWithLikes(sessionJpa.getAccount(), postJpa);
    }

    public PostStats getPostStats(String cookie, UUID postId) {
        if (!authorizationService.isValidSession(cookie)) {
            throw new InvalidCredentialsException("Invalid session cookie");
        }

        PostJpa postJpa = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        long postLikes = postLikeRepository.countByPost(postJpa);
        long numberOfComments = commentRepository.countByPostId(postJpa.getId());

        return new PostStats(fastFibonacci(postLikes + numberOfComments + 50));
    }

    public long fastFibonacci(long n) {
        if (n <= 1) {
            return n;
        }

        long previousTwo = 0;
        long previousOne = 1;
        long current = 0;

        // Build sequence iteratively from 2 up to n
        for (long i = 2; i <= n; i++) {

            // If the index itself is prime,
            // we override the Fibonacci behavior and use i directly.
            if (isPrime(i)) {
                current = i;
            } else {
                // Otherwise compute modified Fibonacci normally
                current = previousOne + previousTwo;
            }

            // Shift values forward for next iteration
            previousTwo = previousOne;
            previousOne = current;
        }

        return current;
    }

    private boolean isPrime(long num) {
        if (num <= 1) {
            return false;
        }
        if (num <= 3) {
            return true;
        }
        if (num % 2 == 0 || num % 3 == 0) {
            return false;
        }

        for (long i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    private Post toPostWithLikes(AccountJpa accountJpa, PostJpa postJpa) {
        long postLikes = postLikeRepository.countByPost(postJpa);
        long numberOfComments = commentRepository.countByPostId(postJpa.getId());
        boolean likedByCurrentUser = postLikeRepository.existsById(PostLikeJpa.create(postJpa, accountJpa).getId());
        return new Post(
            postJpa.getId(),
            postJpa.getAccount().toAccount(),
            postJpa.getContent(),
            postLikes,
            numberOfComments,
            postJpa.getCreatedAt(),
            postJpa.getUpdatedAt(),
            likedByCurrentUser
        );
    }

}
