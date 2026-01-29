package com.group1.froggy.jpa.post.like;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeId {
    private UUID postId;
    private UUID accountId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostLikeId postLikeId = (PostLikeId) o;
        return postId.equals(postLikeId.postId) && accountId.equals(postLikeId.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, accountId);
    }
}
