package com.blog.services;

import com.blog.entities.Image;
import com.blog.entities.Post;
import com.blog.payloads.ImageDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ImageService {
    List<Image> saveAllImages(Post post, MultipartFile[] files);
    void deleteAllImages(List<Image> imageList);
    List<ImageDTO> addImages(Integer postId, MultipartFile[] files);
    List<ImageDTO> getAllImages();
    ImageDTO getImageById(Integer imageId);
    List<Image> findByPost(Post post);
    List<ImageDTO> getAllImagesByPost(Integer postId);
    Map<String, String> deleteImage(Integer imageId) throws IOException;
}
