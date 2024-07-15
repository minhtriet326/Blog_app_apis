package com.blog.services;

import com.blog.payloads.PostDTO;
import com.blog.payloads.PostPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PostService {
    //POST
    PostDTO addPost(PostDTO postDTO, Integer userId, Integer categoryId, MultipartFile file) throws IOException;
    //GET
    List<PostDTO> getAllPosts();
    PostDTO getPostById(Integer postId);
    List<PostDTO> getAllPostsByCategory(Integer categoryId);
    List<PostDTO> getAllPostsByUser(Integer userId);
    List<PostDTO> searchPosts(String keyword);
    PostPageResponse getAllPostsWithPagination(Integer pageNumber, Integer pageSize);
    PostPageResponse getAllPostsWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir);
    //PUT
    PostDTO updatePost(PostDTO postDTO, Integer postId, Integer categoryId, MultipartFile file) throws IOException;
    //DELETE
    Map<String, String> deletePost(Integer postId) throws IOException;
}
