package com.group1.froggy.app.controllers;

import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.docs.returns.MinimalProblemDetail;
import com.group1.froggy.api.docs.returns.MinimalValidationDetail;
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
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller responsible for account signup, login, logout and retrieving the
 * currently authenticated account via the session cookie.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@Tag(name = "Authorization Controller", description = "Handles all operations regarding Authorization")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public static final String COOKIE_HEADER = "Cookie";

    /**
     * Create a new account.
     *
     * @param accountUpload the credentials for the new account
     * @return the created Account
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    @Operation(summary = "Create a new Account", description = "Note you must login after creating the account to get the token.")
    @ApiResponse(responseCode = "201", description = "Account created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "409", description = "Username already exists", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Account createAccount(@RequestBody @NotNull @Valid AccountCredentials accountUpload) {
        return authorizationService.createAccount(accountUpload);
    }

    /**
     * Authenticate an existing account and return a response that includes a
     * Set-Cookie header with the session token on success.
     *
     * @param credentials the account credentials to authenticate
     * @return ResponseEntity with appropriate headers and status
     */
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

    /**
     * Return the currently authenticated account identified by the session
     * cookie provided in the "Cookie" header.
     *
     * @param cookie the raw Cookie header value containing the session token
     * @return the Account associated with the session
     * @throws ResponseStatusException if the cookie is missing or invalid
     */
    @GetMapping
    @Operation(summary = "Get the current logged in Account via the session token cookie")
    @ApiResponse(responseCode = "200", description = "Current account retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Account getCurrentAccount(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return authorizationService.getCurrentAccount(cookie);
    }

    /**
     * Logout the account associated with the provided session cookie. On
     * success a response will be returned that clears the cookie in the
     * browser.
     *
     * @param cookie the raw Cookie header value containing the session token
     * @return ResponseEntity indicating success or failure
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/logout")
    @Operation(summary = "Logout the account from the current session")
    @ApiResponse(responseCode = "200", description = "Successful logout", headers = {@Header(name = "Set-Cookie", description = "Cookie with Max-Age=0 to clear the session from the browser")})
    @ApiResponse(responseCode = "400", description = "Invalid fields provided")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "404", description = "Account not found")
    ResponseEntity<Void> logoutAccount(
        @RequestHeader(value = COOKIE_HEADER, required = false) String cookie
    ) {
        if (cookie == null || cookie.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing session cookie");
        }
        return authorizationService.logoutAccount(cookie);
    }

}
