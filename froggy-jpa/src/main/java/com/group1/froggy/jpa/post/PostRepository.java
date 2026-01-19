package com.group1.froggy.jpa.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostJpa, UUID> {

    @Query(value = "select p from PostJpa p order by p.createdAt desc limit :maxResults")
    List<PostJpa> findLatestPosts(int maxResults);
}
