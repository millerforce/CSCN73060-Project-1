package com.group1.froggy.jpa.account.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionJpa, SessionId> {

    boolean existsByIdAccountIdAndIdToken(UUID accountId, String token);

    void deleteAllByIdAccountId(UUID accountId);

    default boolean sessionExists(UUID accountId, String token) {
        return existsByIdAccountIdAndIdToken(accountId, token);
    }
}
