package com.group1.froggy.api.post;

import com.group1.froggy.api.account.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "Post",
    description = "Represents a post made by an account in the Froggy social platform."
)
public record Post(
    @Schema(description = "The unique identifier of the post")
    @NonNull
    UUID id,

    @Schema(description = "The author of the post")
    @NonNull
    Account author,

    @Schema(description = "The content of the post")
    @NonNull
    String content,

    @Schema(description = "The number of likes the post has received")
    @NonNull
    Long numberOfLikes,

    @Schema(description = "The timestamp when the post was created")
    @NonNull
    LocalDateTime createdAt,

    @Schema(description = "The timestamp when the post was last updated")
    @NonNull
    LocalDateTime updatedAt
) { }
