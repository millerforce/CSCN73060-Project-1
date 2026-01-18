package com.group1.froggy.api.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.util.UUID;

@Schema(
    name = "Session",
    description = "A login session for a account used for authentication and authorization."
)
public record Session(
    @NonNull
    @Schema(description = "The account's ID.")
    UUID accountId,

    @NonNull
    @Schema(description = "The account's authentication token.")
    String token
) { }
