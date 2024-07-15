package com.blog.services;

import com.blog.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();

        // bỏ hoàn toàn tên inage mà sẽ lấy UUID + ".png"
        String randomUUID = UUID.randomUUID().toString();

        // bỏ cả tên, chỉ lấy ".png"
        String name = randomUUID.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

        // UUID + ".png"
        String pathFile = path + File.separator + name;

        File f = new File(path);
        if(!f.exists()) {
            f.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(pathFile));

        return name;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws FileNotFoundException {
        String pathFile = path + File.separator + filename;

        if(!Files.exists(Paths.get(pathFile))) {
            throw new ResourceNotFoundException("File", "name", filename);
        }

        return new FileInputStream(pathFile);
    }
}
