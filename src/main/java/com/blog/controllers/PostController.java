package com.blog.controllers;

import com.blog.exceptions.FileServiceException;
import com.blog.payloads.CategoryDTO;
import com.blog.payloads.PostDTO;
import com.blog.payloads.PostPageResponse;
import com.blog.services.PostService;
import com.blog.utils.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Add a post to database",
            description = "We will add a post to database by providing a postDTO object, userId, categoryId and image file",
            tags = {"Post", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = PostDTO.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping(value = "/addPost", consumes = "multipart/form-data")
    public ResponseEntity<PostDTO> addPostHandler(@RequestPart String postDTOstr,
                                                  @RequestParam Integer userId,
                                                  @RequestParam Integer categoryId,
                                                  @RequestPart(required = false) MultipartFile[] files) throws IOException {

        PostDTO postDTO = convertJSONToPostDTO(postDTOstr);

        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addPost(postDTO, userId, categoryId, files));
    }

    @Operation(
            summary = "Get all Posts from database",
            description = "We will get all Posts from database",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllPosts")
    public ResponseEntity<List<PostDTO>> getAllPostsHandler() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @Operation(
            summary = "Get a specific Post from database",
            description = "We will get a Post from database by Id",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PostDTO.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getPostById/{postId}")
    public ResponseEntity<PostDTO> getPostByIdHandler(@PathVariable Integer postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(postId));
    }

    @Operation(
            summary = "Get all Posts from database by category",
            description = "We will get all Posts from database by providing categoryId",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/getAllPostsByCategory/{categoryId}")
    public ResponseEntity<List<PostDTO>> getAllPostsByCategoryHandler(@PathVariable Integer categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsByCategory(categoryId));
    }

    @Operation(
            summary = "Get all Posts from database by user",
            description = "We will get all Posts from database by providing userId",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllPostsByUser/{userId}")
    public ResponseEntity<List<PostDTO>> getAllPostsByUserHandler(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsByUser(userId));
    }

    @Operation(
            summary = "Search posts via a keyword",
            description = "We will search all posts by providing a keyword",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/searchPosts")
    public ResponseEntity<List<PostDTO>> searchPostsHandler(@RequestParam String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.searchPosts(keyword));
    }

    @Operation(
            summary = "Get all posts on a page",
            description = "We will get all posts on a page by providing page number and page size",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PostPageResponse.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/getAllPostsWithPagination")
    public ResponseEntity<PostPageResponse> getAllPostsWithPaginationHandler(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                             @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsWithPagination(pageNumber, pageSize));
    }

    @Operation(
            summary = "Get all posts on a page and sort them",
            description = "We will get all posts on a page and sort them by providing page number, page size, sort field and direction",
            tags = {"Post", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PostPageResponse.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/getAllPostsWithPaginationAndSorting")
    public ResponseEntity<PostPageResponse> getAllPostsWithPaginationAndSortingHandler(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                                                       @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                                                       @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
                                                                                       @RequestParam(defaultValue = AppConstants.SORT_DIR) String dir) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsWithPaginationAndSorting(
                pageNumber,
                pageSize,
                sortBy,
                dir)
        );
    }

    @Operation(
            summary = "Update a post in the database",
            description = "We will update a post in the database by providing a postId, a postDTO object and a image file",
            tags = {"Post", "put"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PostDTO.class),
                                        mediaType = "application/json")
            )
    })
    @PutMapping(value = "/updatePost/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<PostDTO> updatePostHandler(@RequestPart String postDTOstr,
                                                     @RequestParam Integer postId,
                                                     @RequestParam(required = false) Integer categoryId,
                                                     @RequestPart(required = false) MultipartFile[] files) throws IOException {
        PostDTO postDTO = convertJSONToPostDTO(postDTOstr);
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postDTO, postId, categoryId, files));
    }

    @Operation(
            summary = "Delete a Post from database",
            description = "We will delete a Post from database by providing postId",
            tags = {"Post", "delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class),
                            mediaType = "application/json")
            )
    })
    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<Map<String, String>> deletePostHandler(@PathVariable Integer postId) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId));
    }

    private PostDTO convertJSONToPostDTO(String postDTOstr) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(postDTOstr, PostDTO.class);
    }
}
