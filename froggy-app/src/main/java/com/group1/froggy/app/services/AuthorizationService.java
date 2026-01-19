package com.group1.froggy.app.services;

import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.account.AccountUpload;
import com.group1.froggy.api.account.Session;
import com.group1.froggy.app.CookieBuilder;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.AccountRepository;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.account.session.SessionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorizationService {
    private final AccountRepository accountRepository;
    private final SessionRepository sessionRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static final String SESSION_COOKIE = "session";

    public Account createAccount(AccountUpload accountUpload) {
        if (accountRepository.existsByUsername(accountUpload.username())) {
            throw new EntityExistsException("Account with username already exists");
        }

        String hashedPassword = passwordEncoder.encode(accountUpload.password());

        AccountJpa accountJpa = AccountJpa.create(accountUpload.username(), hashedPassword);

        return accountRepository.save(accountJpa).toAccount();
    }

    public ResponseEntity<Void> loginAccount(AccountCredentials credentials) {
        AccountJpa accountJpa = accountRepository.findByUsername(credentials.username())
            .orElseThrow(() -> new EntityNotFoundException("Account with username does not exist"));

        if (!passwordEncoder.matches(credentials.password(), accountJpa.getHashedPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        SessionJpa sessionJpa = SessionJpa.create(generateToken(), accountJpa);

        sessionJpa = sessionRepository.save(sessionJpa);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, sessionCookie(sessionJpa.toSession()));

        return ResponseEntity.status(200)
            .headers(headers)
            .build();
    }

    public ResponseEntity<Void> logoutAccount(String cookie) {
        Session session = parseSessionCookie(cookie);
        if (session == null) {
            throw new InvalidCredentialsException("No session cookie found");
        }

        if (!sessionRepository.sessionExists(session.accountId(), session.token())) {
            throw new InvalidCredentialsException("Invalid token");
        }

        sessionRepository.deleteById(SessionJpa.createId(session.accountId(), session.token()));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, CookieBuilder.of(SESSION_COOKIE, "")
            .withPath("/")
            .withMaxAge(0)
            .withHttpOnly()
            .build());

        return ResponseEntity.status(204)
            .headers(headers)
            .build();
    }

    private String generateToken() {
        final SecureRandom random = new SecureRandom();
        byte[] token = new byte[64];
        random.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }

    public SessionJpa validateSession(String cookie) {
        if (cookie == null) {
            throw new InvalidCredentialsException("No session cookie found");
        }
        Session session = parseSessionCookie(cookie);
        if (session == null) {
            throw new InvalidCredentialsException("No session cookie found");
        }
        return sessionRepository.findById(SessionJpa.createId(session.accountId(), session.token()))
            .orElseThrow(() -> new InvalidCredentialsException("Invalid token"));
    }

    private static String sessionCookie(Session session) {
        return CookieBuilder.of(SESSION_COOKIE, session.accountId() + ":" + session.token())
            .withPath("/")
            .withHttpOnly()
            .build();
    }

    private static Session parseSessionCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            return null;
        }
        String[] paris = cookie.split(";");
        for (String pair : paris) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].trim().equals(SESSION_COOKIE)) {
                String[] sessionParts = keyValue[1].trim().split(":", 2);
                if (sessionParts.length == 2) {
                    UUID accountId = UUID.fromString(sessionParts[0]);
                    String token = sessionParts[1];
                    return new Session(accountId, token);
                }
            }
        }
        return null;
    }
}
