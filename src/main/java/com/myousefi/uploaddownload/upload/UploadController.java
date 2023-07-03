package com.myousefi.uploaddownload.upload;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {
    private String uploadDir = "src/main/resources/public";

    @PostMapping
    public String upload(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("error1");
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath);
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("error2");
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String name) throws MalformedURLException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(name);
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", name);
            return ResponseEntity.ok().headers(headers).body(resource);
        } else {
            throw new RuntimeException("error2");
        }
    }
}
