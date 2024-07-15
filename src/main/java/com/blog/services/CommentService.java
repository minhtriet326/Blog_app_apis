package com.blog.services;

import com.blog.entities.Comment;
import com.blog.payloads.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface CommentService {
    CommentDTO addComment(CommentDTO commentDTO, Integer postId, String email);
    List<CommentDTO> getAllComments();
    CommentDTO getCommentById(Integer commentId);
    List<CommentDTO> getAllCommentsByPost(Integer postId);
    List<CommentDTO> getAllCommentsByUser(Integer userId);
    CommentDTO updateComment(Integer commentId, CommentDTO commentDTO);
    Map<String, String> deleteComment(Integer commentId);
    CommentDTO commentToDTO(Comment comment);
}
