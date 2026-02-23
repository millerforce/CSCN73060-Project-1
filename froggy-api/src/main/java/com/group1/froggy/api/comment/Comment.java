package com.group1.froggy.api.comment;

import com.group1.froggy.api.account.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "Comment",
    description = "A comment made by an account on a post."
)
public record Comment(
    @Schema(description = "The unique identifier of the comment")
    @NonNull
    UUID id,

    @Schema(description = "The unique identifier of the post the comment belongs to")
    @NonNull
    UUID postId,

    @Schema(description = "The account that made the comment")
    @NonNull
    Account account,

    @Schema(description = "The content of the comment")
    @NonNull
    String content,

    @Schema(description = "The timestamp when the comment was created")
    @NonNull
    LocalDateTime createdAt,

    @Schema(description = "The timestamp when the comment was last updated")
    @NonNull
    LocalDateTime updatedAt,

    @Schema(description = "The number of likes the comment has received")
    @NonNull
    Long numberOfLikes,

    @Schema(description = "Whether the comment is liked by the current user")
    @NonNull
    Boolean likedByCurrentUser
) { }
