package com.blog.services;

import com.blog.entities.Comment;
import com.blog.entities.Post;
import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.CommentDTO;
import com.blog.repositories.CommentRepository;
import com.blog.repositories.PostRepository;
import com.blog.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService{
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentDTO addComment(CommentDTO commentDTO, Integer postId, String email) {

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Comment comment = Comment.builder()
                .commentId(null)
                .content(commentDTO.getContent())
                .addedDate(new Date())
                .lastUpdated(new Date())
                .user(existingUser)
                .post(existingPost)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return commentToDTO(savedComment);
    }

    @Override
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream().map(comment -> commentToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDTO getCommentById(Integer commentId) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", Integer.toString(commentId)));

        return commentToDTO(existingComment);
    }

    @Override
    public List<CommentDTO> getAllCommentsByUser(Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", Integer.toString(userId)));

        return commentRepository.findByUser(existingUser).stream().map(comment -> commentToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getAllCommentsByPost(Integer postId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        return commentRepository.findByPost(existingPost).stream().map(comment -> commentToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDTO updateComment(Integer commentId, CommentDTO commentDTO) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", Integer.toString(commentId)));

        Comment newComment = Comment.builder()
                .commentId(commentId)
                .content(commentDTO.getContent())
                .addedDate(existingComment.getAddedDate())
                .lastUpdated(new Date())
                .user(existingComment.getUser())
                .post(existingComment.getPost())
                .build();

        Comment savedComment = commentRepository.save(newComment);

        return commentToDTO(savedComment);
    }

    @Override
    public Map<String, String> deleteComment(Integer commentId) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", Integer.toString(commentId)));

        commentRepository.delete(existingComment);

        return Map.of("Message", "Comment with Id: " + commentId + " has been deleted successfully!");
    }

    @Override
    public CommentDTO commentToDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .addedDate(comment.getAddedDate())
                .lastUpdated(comment.getLastUpdated())
                .user(comment.getUser().getName())
                .postTitle(comment.getPost().getTitle())
                .build();
    }
}
