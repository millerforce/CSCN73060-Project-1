package com.group1.froggy.app.controllers;

import com.group1.froggy.api.docs.returns.MinimalProblemDetail;
import com.group1.froggy.api.docs.returns.MinimalValidationDetail;
import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.Content;
import com.group1.froggy.api.post.PostStats;
import com.group1.froggy.app.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.group1.froggy.app.controllers.AuthorizationController.COOKIE_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/post")
@Tag(name = "Post Controller", description = "Handles all operations regarding Posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    @Operation(summary = "Get a list of Posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    List<Post> getPosts(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,

        @RequestParam(required = false, defaultValue = "10")
        @Positive(message = "Max results must be positive")
        @Max(value = 100, message = "Max results cannot exceed 100")
        Integer maxResults,

        @RequestParam(required = false, defaultValue = "0")
        @PositiveOrZero(message = "Offset must be positive")
        Integer offset
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return postService.getPosts(cookie, maxResults, offset);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new Post")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Post createPost(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,
        @RequestBody @NotNull(message = "Post data is required") @Valid Content content
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return postService.createPost(cookie, content);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "Edit an existing Post")
    @ApiResponse(responseCode = "200", description = "Post edited successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "403", description = "Only the author can edit the post", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Post editPost(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId,
        @RequestBody @NotNull(message = "Post data is required") @Valid Content content
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return postService.editPost(cookie, postId, content);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete an existing Post")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "403", description = "Only the author can delete the post", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    void deletePost(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        postService.deletePost(cookie, postId);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Like a Post")
    @ApiResponse(responseCode = "200", description = "Post liked successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Post likePost(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return postService.likePost(cookie, postId);
    }

    @GetMapping("/{postId}/stats")
    @Operation(summary = "Get statistics for a Post. Currently only includes a trending score. Designed to be an expensive operation for load testing purposes.")
    @ApiResponse(responseCode = "200", description = "Post statistics retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    PostStats getPostStats(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return postService.getPostStats(cookie, postId);
    }
}
