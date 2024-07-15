package com.blog.controllers;

import com.blog.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${project.images}")
    private String path;

    @Operation(
            summary = "Upload an image file",
            description = "We will upload an image file by providing a multipart file",
            tags = {"File", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = Map.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")// consumes chỉ định datatype mà API này chấp nhận
    public ResponseEntity<Map<String, String>> uploadFileHandler(@RequestPart MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("Message", "File " + fileService.uploadFile(path, file) + " has been uploaded successfully!"));
    }


    @Operation(
            summary = "Watch an uploaded image",
            description = "We will watch an uploaded image by providing filename",
            tags = {"File", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = byte[].class),
                                        mediaType = "image/png")
            )
    })
    @GetMapping(value = "/{filename}", produces = MediaType.IMAGE_PNG_VALUE)// produces chỉ định dataType mà API có thể trả về
    public ResponseEntity<byte[]> serveFileHandler(@PathVariable String filename) throws IOException {
        try {
            InputStream resource = fileService.getResourceFile(path, filename);

            byte[] image = StreamUtils.copyToByteArray(resource);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .body(image);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // build là trả về 1 ResponseEntity rỗng
        }
    }

}
