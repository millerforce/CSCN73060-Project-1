package com.group1.froggy.app.controllers;

import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.account.AccountUpload;
import com.group1.froggy.api.docs.returns.MinimalProblemDetail;
import com.group1.froggy.api.docs.returns.MinimalValidationDetail;
import com.group1.froggy.app.auth.RequireSession;
import com.group1.froggy.app.services.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@Tag(name = "Authorization Controller", description = "Handles all operations regarding Authorization")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public static final String COOKIE_HEADER = "Cookie";

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    @Operation(summary = "Create a new Account", description = "Note you must login after creating the account to get the token.")
    @ApiResponse(responseCode = "201", description = "Account created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "409", description = "Username already exists", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Account createAccount(@RequestBody @NotNull @Valid AccountUpload accountUpload) {
        return authorizationService.createAccount(accountUpload);
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing Account")
    @ApiResponse(responseCode = "200", description = "Successful login, returns the token as a cookie.", headers = {@Header(name = "Set-Cookie", description = "Session token cookie")})
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Account not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    ResponseEntity<Void> loginAccount(
        @RequestBody @NotNull(message = "Credentials are required") @Valid AccountCredentials credentials
    ) {
        return authorizationService.loginAccount(credentials);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/logout")
    @Operation(summary = "Logout the account from the current session")
    @ApiResponse(responseCode = "200", description = "Successful logout", headers = {@Header(name = "Set-Cookie", description = "Cookie with Max-Age=0 to clear the session from the browser")})
    @ApiResponse(responseCode = "400", description = "Invalid fields provided")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @RequireSession
    ResponseEntity<Void> logoutAccount(
        @RequestHeader(COOKIE_HEADER) String cookie
    ) {
        return authorizationService.logoutAccount(cookie);
    }

}
