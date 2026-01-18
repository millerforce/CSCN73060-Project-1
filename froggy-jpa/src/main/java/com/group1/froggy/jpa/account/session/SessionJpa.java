package com.group1.froggy.jpa.account.session;

import com.group1.froggy.api.account.Session;
import com.group1.froggy.jpa.account.AccountJpa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionJpa {

    @EmbeddedId
    private SessionId id;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountJpa account;

    @NonNull
    @Column
    private LocalDateTime createdAt;

    public static SessionJpa create(String token, AccountJpa account) {
        return SessionJpa.builder()
            .id(new SessionId(token, account.getId()))
            .account(account)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static SessionId createId(UUID accountId, String token) {
        return new SessionId(token, accountId);
    }

    public String getToken() {
        return id.getToken();
    }

    public Session toSession() {
        return new Session(account.getId(), id.getToken());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        SessionJpa sessionJpa = (SessionJpa) o;
        return id.equals(sessionJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
