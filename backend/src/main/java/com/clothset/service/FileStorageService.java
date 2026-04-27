package com.clothset.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
    
    public void deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/uploads/")) {
                String fileName = filePath.substring("/uploads/".length());
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Path targetLocation = uploadPath.resolve(fileName);
                Files.deleteIfExists(targetLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file", ex);
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
