package com.group1.froggy.jpa.post;

import com.group1.froggy.api.post.PostContent;
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
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountJpa account;

    @Column
    @NonNull
    private String content;

    @Column
    @NonNull
    private LocalDateTime createdAt;

    @Column
    @NonNull
    private LocalDateTime updatedAt;

    public static PostJpa create(AccountJpa account, PostContent postContent) {
        LocalDateTime createdAt = LocalDateTime.now();

        return PostJpa.builder()
            .account(account)
            .content(postContent.content())
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        PostJpa postJpa = (PostJpa) o;
        return id.equals(postJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
