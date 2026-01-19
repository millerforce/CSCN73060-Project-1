package com.group1.froggy.jpa.post;

import com.group1.froggy.api.post.Post;
import com.group1.froggy.api.post.PostUpload;
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

    @Builder.Default
    @Column
    @NonNull
    private Long numberOfLikes = 0L;

    @Builder.Default
    @Column
    @NonNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public static PostJpa create(AccountJpa account, PostUpload postUpload) {
        return PostJpa.builder()
            .account(account)
            .content(postUpload.content())
            .build();
    }

    public Post toPost() {
        return new Post(id, account.toAccount(), content, numberOfLikes, createdAt);
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
