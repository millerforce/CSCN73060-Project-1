package com.group1.froggy.jpa.account;

import com.group1.froggy.api.account.Account;
import com.group1.froggy.api.account.AccountUpload;
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
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    @Column
    private String hashedPassword;

    @Builder.Default
    @Column
    @NonNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public static AccountJpa create(String username, String hashedPassword) {
        return AccountJpa.builder()
            .username(username)
            .hashedPassword(hashedPassword)
            .build();
    }

    public Account toAccount() {
        return new Account(id, username, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        AccountJpa accountJpa = (AccountJpa) o;
        return id.equals(accountJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
