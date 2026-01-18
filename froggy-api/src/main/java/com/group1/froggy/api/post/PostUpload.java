package com.group1.froggy.api.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "PostUpload",
    description = "Data required to create a new post."
)
public record PostUpload(
    @Schema(description = "The title of the post")
    @NonNull
    String content
) { }
