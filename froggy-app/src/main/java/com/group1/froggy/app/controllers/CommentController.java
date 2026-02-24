package com.group1.froggy.app.controllers;

import com.group1.froggy.api.Content;
import com.group1.froggy.api.comment.Comment;
import com.group1.froggy.api.docs.returns.MinimalProblemDetail;
import com.group1.froggy.api.docs.returns.MinimalValidationDetail;
import com.group1.froggy.app.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/comment")
@Tag(name = "Comment Controller", description = "Handles all operations regarding Comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get all comments for a post", description = "Retrieves a list of comments associated with a specific post.")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @GetMapping("{postId}")
    List<Comment> getCommentsByPost(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,

        @NotNull(message = "Post ID cannot be null")
        @PathVariable UUID postId
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return commentService.getCommentsByPost(cookie, postId);
    }

    @Operation(summary = "Create a new comment for a post", description = "Creates a new comment associated with a specific post. Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Post not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{postId}")
    Comment createComment(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,

        @PathVariable
        @NotNull(message = "Post ID cannot be null")
        UUID postId,

        @RequestBody
        @NotNull(message = "Content cannot be null")
        @Valid
        Content content
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return commentService.createComment(cookie, postId, content);
    }

    @Operation(summary = "Edit an existing comment", description = "Edits the content of an existing comment. Only the author of the comment can edit it. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Comment edited successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "403", description = "Only the author can edit the comment", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Comment not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @PatchMapping("{commentId}")
    Comment editComment(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,

        @PathVariable
        @NotNull(message = "Comment ID cannot be null")
        UUID commentId,

        @RequestBody
        @NotNull(message = "Content cannot be null")
        @Valid
        Content content
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return commentService.editComment(cookie, commentId, content);
    }

    @Operation(summary = "Delete an existing comment", description = "Deletes an existing comment. Only the author of the comment can delete it. Requires authentication.")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "403", description = "Only the author can delete the comment", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Comment not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,

        @PathVariable
        @NotNull(message = "Comment ID cannot be null")
        UUID commentId
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        commentService.deleteComment(cookie, commentId);
    }

    @Operation(summary = "Like a comment", description = "Likes a comment. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Comment liked successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Comment not found", content = {@io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @PutMapping("{commentId}")
    Comment likeComment(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie,

        @PathVariable
        @NotNull(message = "Comment ID cannot be null")
        UUID commentId
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return commentService.likeComment(cookie, commentId);
    }


}
