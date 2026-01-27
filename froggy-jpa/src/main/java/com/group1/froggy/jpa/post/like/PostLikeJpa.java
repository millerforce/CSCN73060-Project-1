package com.group1.froggy.jpa.post.like;

import com.group1.froggy.jpa.account.AccountJpa;
import com.group1.froggy.jpa.post.PostJpa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_like")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostLikeJpa {
    @EmbeddedId
    private PostLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    @NonNull
    private PostJpa post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountJpa account;

    public static PostLikeJpa create(PostJpa post, AccountJpa account) {
        PostLikeId id = new PostLikeId(post.getId(), account.getId());
        return PostLikeJpa.builder()
            .id(id)
            .post(post)
            .account(account)
            .build();
    }
}
