package com.group1.froggy.api.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "Statistics related to a Post.")
public record PostStats(
    @NonNull
    @Schema(description = "A score representing the popularity of the post.")
    Long trendingScore
) { }
