package com.group1.froggy.jpa.account.session;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionId implements Serializable {

    @NonNull
    private String token;

    @NonNull
    private UUID accountId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SessionId sessionId = (SessionId) o;
        return token.equals(sessionId.token) && accountId.equals(sessionId.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, accountId);
    }
}
