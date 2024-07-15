package com.blog.services;

import com.blog.entities.Category;
import com.blog.entities.Post;
import com.blog.entities.User;
import com.blog.exceptions.FileServiceException;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.PostDTO;
import com.blog.payloads.PostPageResponse;
import com.blog.repositories.CategoryRepository;
import com.blog.repositories.PostRepository;
import com.blog.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final CommentService commentService;

    public PostServiceImpl(PostRepository postRepository, ModelMapper modelMapper, UserRepository userRepository, CategoryRepository categoryRepository, FileService fileService, CommentService commentService) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.commentService = commentService;
    }

    @Value("${project.images}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public PostDTO addPost(PostDTO postDTO,
                           Integer userId,
                           Integer categoryId,
                           MultipartFile file) throws IOException {

        String filename = "default.png";

        if(file != null) {
            if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
                throw new FileServiceException("Filename is already existed! Please enter another filename.");
            }

            filename = fileService.uploadFile(path, file);
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", Integer.toString(userId)));

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", Integer.toString(categoryId)));

        //create Post object
        Post post = Post.builder()
                .postId(null)
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .imageName(filename)
                .addedDate(new Date())
                .lastUpdated(new Date())
                .user(existingUser)
                .comments(new HashSet<>())
                .category(existingCategory)
                .build();

        Post savedPost = postRepository.save(post);

        //create imageUrl
        String imageUrl = filename.equals("default.png") ? null : baseUrl + "/file/" + filename;

        //create PostDTO object
        return postToDTO(savedPost);
    }

    @Override
    public List<PostDTO> getAllPosts() {

        List<Post> posts = postRepository.findAll();

        List<PostDTO> postDTOS = posts.stream().map(post -> postToDTO(post)).collect(Collectors.toList());

        return postDTOS;
    }

    @Override
    public PostDTO getPostById(Integer postId) {

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        return postToDTO(existingPost);
    }

    @Override
    public List<PostDTO> getAllPostsByCategory(Integer categoryId) {

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", Integer.toString(categoryId)));

        List<Post> posts = postRepository.findByCategory(existingCategory);
        // nếu không tìm thấy bất kỳ post nào, thì nó sẽ trả về một empty list

        List<PostDTO> postDTOS = posts.stream().map(post -> postToDTO(post)).collect(Collectors.toList());
        // này empty theo luôn

        return postDTOS;
    }

    @Override
    public List<PostDTO> getAllPostsByUser(Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", Integer.toString(userId)));

        List<Post> posts = postRepository.findByUser(existingUser);
        // nếu không tìm thấy bất kỳ post nào, thì nó sẽ trả về một empty list

        List<PostDTO> postDTOS = posts.stream().map(post -> postToDTO(post)).collect(Collectors.toList());
        // này empty theo luôn

        return postDTOS;
    }

    @Override
    public List<PostDTO> searchPosts(String keyword) {

        //Biểu thức [[:<:]]keyword[[:>:]] đảm bảo chỉ tìm kiếm các từ chính xác.
        String formattedKeyword = "[[:<:]]" + keyword + "[[:>:]]";

        List<Post> posts = postRepository.searchPostsByKeyword(formattedKeyword);

//        List<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword);

        List<PostDTO> postDTOS = posts.stream().map(this::postToDTO).collect(Collectors.toList());

        return postDTOS;
    }

    @Override
    public PostPageResponse getAllPostsWithPagination(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Post> postPage = postRepository.findAll(pageable);

        List<Post> posts = postPage.getContent();

        List<PostDTO> postDTOS = posts.stream().map(post -> postToDTO(post)).collect(Collectors.toList());

        return new PostPageResponse(
                postDTOS,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );
    }

    @Override
    public PostPageResponse getAllPostsWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                                String sortBy, String dir) {

//        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Sort sort = dir.equalsIgnoreCase("asc") ?
                Sort.by(Sort.Direction.ASC, sortBy) :
                Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Post> postPage = postRepository.findAll(pageable);

        List<Post> posts = postPage.getContent();

        List<PostDTO> postDTOS = posts.stream().map(this::postToDTO).collect(Collectors.toList());
        // method reference: ClassName::methodName nghĩa là tên của cái class(Classname) chứa method đó(methodName)

        return new PostPageResponse(
                postDTOS,
                pageNumber,
                pageSize,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast());
    }

    @Override
    public PostDTO updatePost(PostDTO postDTO, Integer postId, Integer categoryId, MultipartFile file) throws IOException {

        // kiểm tra post tồn tại
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        // lấy category cũ nếu categoryId == null
        Category setCategory;

        if(categoryId == null) {

            setCategory = existingPost.getCategory();

        } else {

            // kiểm tra category tồn tại
            setCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", Integer.toString(categoryId)));
        }

        // lấy tên file cũ
        String filename = existingPost.getImageName();

        if(file != null) {
//            if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
//
//                if(file.getOriginalFilename().equals(existingPost.getImageName())) {
//
//                    Files.deleteIfExists(Paths.get(path + File.separator + existingPost.getImageName()));
//
//                    filename = fileService.uploadFile(path, file);
//
//                } else {
//                    throw new FileServiceException("Filename is already existed! Please enter another filename.");
//                }
//
//            } else {
//                filename = fileService.uploadFile(path, file);
//            }
            Files.deleteIfExists(Paths.get(path + File.separator + existingPost.getImageName()));

            filename = fileService.uploadFile(path, file);
        }

        Post updatedPost = Post.builder()
                .postId(postId)
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .imageName(filename)
                .addedDate(existingPost.getAddedDate())
                .lastUpdated(new Date())
                .user(existingPost.getUser())
                .comments(existingPost.getComments())
                .category(setCategory)
                .build();

        Post savedPost = postRepository.save(updatedPost);

        return postToDTO(savedPost);
    }

    @Override
    public Map<String, String> deletePost(Integer postId) throws IOException {

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        Files.deleteIfExists(Paths.get(path + File.separator + existingPost.getImageName()));

        postRepository.delete(existingPost);

        return Map.of("Message", "Post with postId " + postId + " has been deleted successfully!");
    }

    private PostDTO postToDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageName(post.getImageName())
                .addedDate(post.getAddedDate())
                .lastUpdated(post.getLastUpdated())
                .user(post.getUser().getName())
                .commentDTOList(post.getComments().stream().map(
                        commentService::commentToDTO).collect(Collectors.toList()))
                .category(post.getCategory().getTitle())
                .imageUrl(baseUrl + "/file/" + post.getImageName())
                .build();
    }
}
