package com.blog.controllers;

import com.blog.auth.services.JwtService;
import com.blog.payloads.CommentDTO;
import com.blog.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final JwtService jwtService;

    public CommentController(CommentService commentService, JwtService jwtService) {
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Add a comment to a post",
            description = "We will add a comment to a post by providing a postId and a CommentDTO object",
            tags = {"Comment", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping("/addComment/{postId}")
    public ResponseEntity<CommentDTO> addCommentHandler(@PathVariable Integer postId,
                                                        @Valid @RequestBody CommentDTO commentDTO,
                                                        @NonNull HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        String email = jwtService.extractEmail(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(commentDTO, postId, email));
    }

    @Operation(
            summary = "Get all comments of a post",
            description = "We will get all comments of a post",
            tags = {"Comment", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllComments")
    public ResponseEntity<List<CommentDTO>> getAllCommentsHandler() {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComments());
    }

    @Operation(
            summary = "Get a specific comment of a post via commentId",
            description = "We will get a specific comment of a post by providing a commentId",
            tags = {"Comment", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getCommentById/{commentId}")
    public ResponseEntity<CommentDTO> getCommentByIdHandler(@PathVariable Integer commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentById(commentId));
    }

    @Operation(
            summary = "Get all comments of a post via userId",
            description = "We will get all comments of a post by providing a userId",
            tags = {"Comment", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllCommentsByUser/{userId}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByUserHandler(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByUser(userId));
    }

    @Operation(
            summary = "Get all comments of a post via postId",
            description = "We will get all comments of a post by providing a postId",
            tags = {"Comment", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllCommentsByPost/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByPostHandler(@PathVariable Integer postId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByPost(postId));
    }

    @Operation(
            summary = "Update a comment of a post",
            description = "We will update a specific comment of a post by providing a commentId and a CommentDTO object",
            tags = {"Comment", "put"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDTO.class),
                            mediaType = "application/json")
            )
    })
    @PutMapping("/updateComment/{commentId}")
    public ResponseEntity<CommentDTO> updateCommentHandler(@PathVariable Integer commentId,
                                                            @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(commentId, commentDTO));
    }

    @Operation(
            summary = "Delete a comment of a post",
            description = "We will delete a specific comment of a post by providing a commentId",
            tags = {"Comment", "delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class),
                            mediaType = "application/json")
            )
    })
    @DeleteMapping("/deleteComment/{commentId}")
    public ResponseEntity<Map<String, String>> deleteCommentHandler(@PathVariable Integer commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(commentId));
    }
}
