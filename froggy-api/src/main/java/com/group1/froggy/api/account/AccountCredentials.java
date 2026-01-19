package com.group1.froggy.api.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
    name = "AccountCredentials",
    description = "The credentials required to authenticate a account."
)
public record AccountCredentials(
    @Schema(description = "The account's username.")
    @NotNull(message = "Username is required")
    String username,

    @Schema(description = "The account's password.")
    @NotNull(message = "Password is required")
    String password
) { }
