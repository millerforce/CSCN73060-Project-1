package com.group1.froggy.app.services;

import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountCredentials;
import com.group1.froggy.api.account.Session;
import com.group1.froggy.app.exceptions.InvalidCredentialsException;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.account.AccountRepository;
import com.group1.froggy.jpa.account.session.SessionJpa;
import com.group1.froggy.jpa.account.session.SessionRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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
        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
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
        AccountCredentials accountUpload = new AccountCredentials("eighdyy", "123");
        when(accountRepository.existsByUsername(accountUpload.username()))
                .thenReturn(true);

        assertThrows(EntityExistsException.class, () -> authorizationService.createAccount(accountUpload));
    }

    @Test
    void loginAccount_Success(){

        AccountCredentials accountUpload = new AccountCredentials("willy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(new BCryptPasswordEncoder().encode(accountUpload.password()))
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername(accountUpload.username())).thenReturn(Optional.of(accountJpa));
        when(sessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        ResponseEntity<Void> response = authorizationService.loginAccount(accountUpload);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie);
        verify(sessionRepository).save(any(SessionJpa.class));
    }

    @Test
    void loginAccount_InvalidPassword(){
        AccountCredentials accountBadPassword = new AccountCredentials("Connor", "bad");
        AccountCredentials accountUpload = new AccountCredentials("Connor", "good");

        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(new BCryptPasswordEncoder().encode(accountUpload.password()))
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername(accountUpload.username())).thenReturn(Optional.of(accountJpa));

        assertThrows(InvalidCredentialsException.class, () -> authorizationService.loginAccount(accountBadPassword));
    }


    @Test
    void logoutAccount_Success(){
        AccountCredentials accountUpload = new AccountCredentials("willy", "123");
        AccountJpa accountJpa = AccountJpa.builder()
                .id(UUID.randomUUID())
                .username(accountUpload.username())
                .hashedPassword(new BCryptPasswordEncoder().encode(accountUpload.password()))
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByUsername(accountUpload.username())).thenReturn(Optional.of(accountJpa));
        when(sessionRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(sessionRepository.sessionExists(any(), any())).thenReturn(true);

        ResponseEntity<Void> response = authorizationService.loginAccount(accountUpload);
        assertNotNull(response);

        response = authorizationService.logoutAccount(response.getHeaders().getFirst("Set-Cookie"));

        assertEquals("session=; Path=/; Max-Age=0; HttpOnly", response.getHeaders().getFirst("Set-Cookie"));
    }
}
