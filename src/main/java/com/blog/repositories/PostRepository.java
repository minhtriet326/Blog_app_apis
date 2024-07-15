package com.blog.repositories;

import com.blog.entities.Category;
import com.blog.entities.Post;
import com.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUser(User user);
    List<Post> findByCategory(Category category);
    @Query(value = "SELECT * FROM posts p WHERE p.content REGEXP :keyword", nativeQuery = true)
    // cái này là biểu thức chính quy, tức là SQL gốc
    List<Post> searchPostsByKeyword(@Param("keyword") String keyword);
    List<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);
}
