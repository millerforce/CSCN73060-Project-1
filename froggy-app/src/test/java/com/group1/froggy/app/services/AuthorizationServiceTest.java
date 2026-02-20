package com.group1.froggy.app.services;

import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.account.AccountUpload;
import com.group1.froggy.api.account.Session;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.AccountRepository;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.account.session.SessionRepository;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class AuthorizationServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private  AccountRepository accountRepository;

    @InjectMocks
    private  AuthorizationService authorizationService;

    @Test
    void createAccount_Success(){
        AccountUpload accountUpload = new AccountUpload("eighdyy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(accountUpload.password())
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.existsByUsername(accountUpload.username()))
                .thenReturn(false);
        when(accountRepository.save(any(AccountJpa.class)))
                .thenReturn(accountJpa);

        Account account = authorizationService.createAccount(accountUpload);

        assertNotNull(account);
        verify(accountRepository).save(any(AccountJpa.class));
    }

    @Test
    void createAccount_UsernameExists(){
        AccountUpload accountUpload = new AccountUpload("eighdyy", "123");
        when(accountRepository.existsByUsername(accountUpload.username()))
                .thenReturn(true);

        assertThrows(EntityExistsException.class, () -> authorizationService.createAccount(accountUpload));
    }

    @Test
    void loginAccount_Success(){
        String username = "willy";
        String rawPassword = "123";

        AccountJpa accountJpa = mock(AccountJpa.class);
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(accountJpa));
        when(accountJpa.getHashedPassword())
                .thenReturn(new BCryptPasswordEncoder().encode(rawPassword));
        when(sessionRepository.save(any(SessionJpa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountCredentials accountCredentials = new AccountCredentials(username, rawPassword);
        ResponseEntity<Void> response = authorizationService.loginAccount(accountCredentials);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie);
        verify(sessionRepository).save(any(SessionJpa.class));
    }

    @Test
    void loginAccount_InvalidCredentials(){
        String username = "Connor";
        String rawPassword = "BADConnor";

        AccountJpa accountjpa = mock(AccountJpa.class);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(accountjpa));
        when(accountjpa.getHashedPassword())
                .thenReturn(new BCryptPasswordEncoder().encode(rawPassword));
        when(sessionRepository.save(any(SessionJpa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountCredentials accountCredentials = new AccountCredentials(username, rawPassword);

        assertThrows(InvalidCredentialsException.class, () -> authorizationService.loginAccount(accountCredentials));
    }

    @Test
    void logoutAccount_Success(){

        String username = "port 8080";
        String rawPassword = "123";

        UUID accountId = UUID.randomUUID();
        String token = "validToken";

        when(sessionRepository.sessionExists(accountId, token)).thenReturn(true);

        AccountCredentials accountCredentials = new AccountCredentials(username, rawPassword);
        ResponseEntity<Void> response = authorizationService.loginAccount(accountCredentials);

        assertNull(response);
    }
}
