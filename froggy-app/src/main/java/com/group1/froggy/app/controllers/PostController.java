package com.group1.froggy.app.controllers;

import com.group1.froggy.api.docs.returns.MinimalProblemDetail;
import com.group1.froggy.api.docs.returns.MinimalValidationDetail;
import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.post.PostContent;
import com.group1.froggy.app.auth.RequireSession;
import com.group1.froggy.app.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.util.List;
import java.util.UUID;

import static com.group1.froggy.app.controllers.AuthorizationController.COOKIE_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/post")
@Tag(name = "Post Controller", description = "Handles all operations regarding Posts")
@RequiredArgsConstructor
@RequireSession
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    @Operation(summary = "Get a list of Posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    List<Post> getPosts(
        @RequestHeader(COOKIE_HEADER) String cookie,

        @RequestParam(required = false, defaultValue = "10")
        @Positive(message = "Max results must be positive")
        @Max(value = 100, message = "Max results cannot exceed 100")
        Integer maxResults,

        @RequestParam(required = false, defaultValue = "0")
        @PositiveOrZero(message = "Offset must be positive")
        Integer offset
    ) {
        return postService.getPosts(cookie, maxResults, offset);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new Post")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Post createPost(
        @RequestHeader(COOKIE_HEADER) String cookie,
        @RequestBody @NotNull(message = "Post data is required") @Valid PostContent postContent
    ) {
        return postService.createPost(cookie, postContent);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "Edit an existing Post")
    @ApiResponse(responseCode = "200", description = "Post edited successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "403", description = "Only the author can edit the post", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Post editPost(
        @RequestHeader(COOKIE_HEADER) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId,
        @RequestBody @NotNull(message = "Post data is required") @Valid PostContent postContent
    ) {
        return postService.editPost(cookie, postId, postContent);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete an existing Post")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "403", description = "Only the author can delete the post", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    void deletePost(
        @RequestHeader(COOKIE_HEADER) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId
    ) {
        postService.deletePost(cookie, postId);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Like a Post")
    @ApiResponse(responseCode = "200", description = "Post liked successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Post likePost(
        @RequestHeader(COOKIE_HEADER) String cookie,
        @PathVariable @NotNull(message = "Post ID is required") UUID postId
    ) {
        return postService.likePost(cookie, postId);
    }
}
