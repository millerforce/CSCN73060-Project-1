package com.group1.froggy.jpa.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountJpa, UUID> {

    Optional<AccountJpa> findByUsername(String username);

    boolean existsByUsername(String username);
}
