package com.hits.FileSystem.Services;

import com.hits.FileSystem.Models.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IMinIOService {
    ResponseEntity<?> uploadFile(User user, MultipartFile file) throws IOException;
    ResponseEntity<?> downloadFile(User user, UUID id) throws Exception;
    ResponseEntity<?> getAllFiles(User user) throws IOException;
}
