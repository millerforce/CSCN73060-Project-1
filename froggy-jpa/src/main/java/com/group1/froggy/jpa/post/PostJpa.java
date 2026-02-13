package com.group1.froggy.jpa.post;

import com.group1.froggy.api.Content;
import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.post.comment.CommentJpa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Builder
@ToString
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountJpa account;

    @Setter
    @Column
    @NonNull
    private String content;

    @Column
    @NonNull
    private LocalDateTime createdAt;

    @Setter
    @Column
    @NonNull
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CommentJpa> comments = new ArrayList<>();

    public static PostJpa create(AccountJpa account, Content content) {
        LocalDateTime createdAt = LocalDateTime.now();

        return PostJpa.builder()
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

        PostJpa postJpa = (PostJpa) o;
        return id.equals(postJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
