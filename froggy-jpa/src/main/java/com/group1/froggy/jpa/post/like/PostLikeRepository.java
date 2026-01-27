package com.group1.froggy.jpa.post.like;

import com.group1.froggy.jpa.post.PostJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeJpa, PostLikeId> {
    long countByPost(PostJpa post);
    void deleteAllByPost(PostJpa post);
}
