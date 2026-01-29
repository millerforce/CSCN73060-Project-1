package com.group1.froggy.jpa.post.comment;

import com.group1.froggy.api.Content;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.post.PostJpa;
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
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @NonNull
    private PostJpa post;

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

    public static CommentJpa create(PostJpa post, AccountJpa account, Content content) {
        LocalDateTime createdAt = LocalDateTime.now();

        return CommentJpa.builder()
            .post(post)
            .account(account)
            .content(content.content())
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

        CommentJpa postJpa = (CommentJpa) o;
        return id.equals(postJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
