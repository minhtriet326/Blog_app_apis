package com.blog.services;

import com.blog.entities.Image;
import com.blog.entities.Post;
import com.blog.exceptions.FileServiceException;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.ImageDTO;
import com.blog.repositories.ImageRepository;
import com.blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService{
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final FileService fileService;

    public ImageServiceImpl(ImageRepository imageRepository, PostRepository postRepository, FileService fileService) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
        this.fileService = fileService;
    }

    @Value("${project.images}")
    private String path;

    @Override
    public List<Image> saveAllImages(Post post, MultipartFile[] files) {

            List<Image> imageList = new ArrayList<>();

            Arrays.stream(files).forEach(file -> {
                try {
                    // tạo từng image entity va dua vao mang imageList
                    Image image = Image.builder()
                            .imageName(fileService.uploadFile(path, file))
                            .post(post)
                            .build();

                    imageList.add(image);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        return imageRepository.saveAll(imageList);
    }

    @Override
    public void deleteAllImages(List<Image> imageList) {
        imageRepository.deleteAll(imageList);
    }

    @Override
    public List<ImageDTO> addImages(Integer postId, MultipartFile[] files) {

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        int quantityOfImages = existingPost.getImages().size();

        if (quantityOfImages + files.length > 5) {

            throw new FileServiceException("Can not upload more than 5 images");

        } else {

            List<Image> imageList = new ArrayList<>();

            if (files != null) {
                imageList = saveAllImages(existingPost, files);
            }

            return imageList.stream().map(ImageServiceImpl::imageToDTO).collect(Collectors.toList());
        }
    }

    @Override
    public List<ImageDTO> getAllImages() {
        return imageRepository.findAll()
                .stream().map(ImageServiceImpl::imageToDTO).collect(Collectors.toList());
    }

    @Override
    public ImageDTO getImageById(Integer imageId) {

        Image existingImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "imageId", Integer.toString(imageId)));

        return imageToDTO(existingImage);
    }

    @Override
    public List<Image> findByPost(Post post) {
        return imageRepository.findByPost(post);
    }

    @Override
    public List<ImageDTO> getAllImagesByPost(Integer postId) {

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Integer.toString(postId)));

        return existingPost.getImages().stream().map(ImageServiceImpl::imageToDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> deleteImage(Integer imageId) throws IOException {

        Image existingImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "imageId", Integer.toString(imageId)));

        // delete image in local
        Files.deleteIfExists(Paths.get(path + File.separator + existingImage.getImageName()));

        // delete Image in database
        imageRepository.delete(existingImage);

        return Map.of("Message", "Image with id " + imageId + " has been deleted successfully!");
    }

    private static ImageDTO imageToDTO(Image image) {
        return ImageDTO.builder()
                .imageId(image.getImageId())
                .imageName(image.getImageName())
                .postTitle(image.getPost().getTitle())
                .build();
    }
}
