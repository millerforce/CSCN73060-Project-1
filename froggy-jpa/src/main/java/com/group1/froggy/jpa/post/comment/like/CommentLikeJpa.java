package com.group1.froggy.jpa.post.comment.like;

import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.post.comment.CommentJpa;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment_like")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CommentLikeJpa {
    @EmbeddedId
    private CommentLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    @NonNull
    private CommentJpa comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountJpa account;

    public static CommentLikeJpa create(CommentJpa commentJpa, AccountJpa account) {
        CommentLikeId id = new CommentLikeId(commentJpa.getId(), account.getId());
        return CommentLikeJpa.builder()
            .id(id)
            .comment(commentJpa)
            .account(account)
            .build();
    }
}
