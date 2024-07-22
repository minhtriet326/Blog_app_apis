package com.blog.controllers;

import com.blog.payloads.ImageDTO;
import com.blog.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(
            summary = "Add an image to a Post",
            description = "We will add an image to a Post by providing postId and up to 5 image files",
            tags = {"Image", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "210",
                    content = @Content(schema = @Schema(implementation = List.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping(value = "/addImages/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<List<ImageDTO>> addImagesHandler(@PathVariable Integer postId,
                                                           @RequestPart MultipartFile[] files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.addImages(postId, files));
    }

    @Operation(
            summary = "Get all images",
            description = "We will get all images from all posts",
            tags = {"Image", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/getAllImages")
    public ResponseEntity<List<ImageDTO>> getAllImagesHandler() {
        return ResponseEntity.status(HttpStatus.OK).body(imageService.getAllImages());
    }

    @Operation(
            summary = "Get an image by id",
            description = "We will get a specific image by providing imageId",
            tags = {"Image", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ImageDTO.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/getImageById/{imageId}")
    public ResponseEntity<ImageDTO> getImageByIdHandler(@PathVariable Integer imageId) {
        return ResponseEntity.status(HttpStatus.OK).body(imageService.getImageById(imageId));
    }

    @Operation(
            summary = "Get all images from a Post",
            description = "We will get all images from a Post by providing postId",
            tags = {"Image", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = List.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/getAllImagesByPost/{postId}")
    public ResponseEntity<List<ImageDTO>> getAllImagesByPostHandler(@PathVariable Integer postId) {
        return ResponseEntity.status(HttpStatus.OK).body(imageService.getAllImagesByPost(postId));
    }

    @Operation(
            summary = "Delete an image",
            description = "We will delete a specific image by providing imageId",
            tags = {"Image", "delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class),
                                        mediaType = "application/json")
            )
    })
    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<Map<String, String>> deleteImageHandler(@PathVariable Integer imageId) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(imageService.deleteImage(imageId));
    }
}
