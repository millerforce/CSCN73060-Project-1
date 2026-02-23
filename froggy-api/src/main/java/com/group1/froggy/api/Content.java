package com.group1.froggy.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "Content",
    description = "Data required to create or edit a post/comment."
)
public record Content(
    @Schema(description = "The content of the post/comment")
    @NonNull
    String content
) { }
