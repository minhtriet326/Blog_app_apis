package com.blog.repositories;

import com.blog.entities.Comment;
import com.blog.entities.Post;
import com.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByUser(User user);
    List<Comment> findByPost(Post post);
}
