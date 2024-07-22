package com.blog.services;

import com.blog.entities.Category;
import com.blog.entities.Image;
import com.blog.entities.Post;
import com.blog.entities.User;
import com.blog.exceptions.FileServiceException;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.PostDTO;
import com.blog.payloads.PostPageResponse;
import com.blog.repositories.CategoryRepository;
import com.blog.repositories.ImageRepository;
import com.blog.repositories.PostRepository;
import com.blog.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final CommentService commentService;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository, ImageService imageService, CommentService commentService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
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
                           MultipartFile[] files) throws IOException {

        if (files != null && files.length > 5) {
            throw new FileServiceException("Can not upload more than 5 images");
        }

        // 1-tạo 1 đt Post trước
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", Integer.toString(userId)));

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", Integer.toString(categoryId)));

        Post post = Post.builder()
                .postId(null)
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .images(new ArrayList<>())
                .addedDate(new Date())
                .lastUpdated(new Date())
                .user(existingUser)
                .comments(new HashSet<>())
                .category(existingCategory)
                .build();

        Post firstSavedPost = postRepository.save(post);

        // 2-tạo và lưu các đt Image
        List<Image> imageList = new ArrayList<>();

        if (files != null) {
            imageList = imageService.saveAllImages(firstSavedPost, files);
        }

        firstSavedPost.setImages(imageList);

        Post lastSavedPost = postRepository.save(firstSavedPost);

        return postToDTO(lastSavedPost);
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
    public PostDTO updatePost(PostDTO postDTO, Integer postId, Integer categoryId, MultipartFile[] files) throws IOException {

        // Kiểm tra sl ảnh upload phải <= 5
        if(files != null && files.length > 5) {
            throw new FileServiceException("Can not upload more than 5 images");
        }

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

        // upload ảnh mới và xóa hết ảnh cũ
        if (files != null) {
            // store all Image object which will be deleted
            List<Image> deleteImage = imageService.findByPost(existingPost);

            // delete in local
            deleteImage.forEach(image ->
            {
                try {
                    // xóa các ảnh lưu local
                    Files.deleteIfExists(Paths.get(path + File.separator + image.getImageName()));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // delete all old images
            imageService.deleteAllImages(deleteImage);

            // save all new images
            imageService.saveAllImages(existingPost, files);
        }

        // update Post
        existingPost.setTitle(postDTO.getTitle());
        existingPost.setContent(postDTO.getContent());
        existingPost.setLastUpdated(new Date());
        existingPost.setCategory(setCategory);

        Post savedPost = postRepository.save(existingPost);

        return postToDTO(savedPost);
    }

    @Override
    public Map<String, String> deletePost(Integer postId) throws IOException {

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        existingPost.getImages().forEach(image ->
        {
            try {
                Files.deleteIfExists(Paths.get(path + File.separator + image.getImageName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        postRepository.delete(existingPost);

        return Map.of("Message", "Post with postId " + postId + " has been deleted successfully!");
    }

    private PostDTO postToDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageNames(post.getImages().stream().map(
                        Image::getImageName).collect(Collectors.toList()))
                .addedDate(post.getAddedDate())
                .lastUpdated(post.getLastUpdated())
                .user(post.getUser().getName())
                .commentDTOList(post.getComments().stream().map(
                        commentService::commentToDTO).collect(Collectors.toList()))
                .category(post.getCategory().getTitle())
                .imageUrl(post.getImages().stream().map(
                        image -> baseUrl + "/file/" + image.getImageName()
                ).collect(Collectors.toList()))
                .build();
    }
}
