package com.group1.froggy.api.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
    name = "Account",
    description = "The main entity representing a user account in the system."
)
public record Account(
    @Schema(description = "The account's unique identifier")
    @NonNull
    UUID id,

    @Schema(description = "The account's username")
    @NonNull
    String username,

    @Schema(description = "The date and time the account was created")
    @NonNull
    LocalDateTime createdAt
) { }
