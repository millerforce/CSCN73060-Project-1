package com.group1.froggy.api.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "PostUpload",
    description = "Data required to create or edit a post."
)
public record PostContent(
    @Schema(description = "The content of the post")
    @NonNull
    String content
) { }
