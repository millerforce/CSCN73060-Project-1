package com.group1.froggy.api.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "AccountUpload",
    description = "Data required to create a new account"
)
public record AccountUpload(
    @Schema(description = "The desired username for the account")
    @NonNull
    String username,

    @Schema(description = "The desired password for the account")
    @NonNull
    String password
) { }
