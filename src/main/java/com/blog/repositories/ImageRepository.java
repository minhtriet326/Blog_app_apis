package com.blog.repositories;

import com.blog.entities.Image;
import com.blog.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByPost(Post post);
}
