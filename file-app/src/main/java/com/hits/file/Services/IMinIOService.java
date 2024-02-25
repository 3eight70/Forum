package com.hits.file.Services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IMinIOService {
    ResponseEntity<?> uploadFile(String token, MultipartFile file) throws IOException;
    ResponseEntity<?> downloadFile(String token, UUID id) throws Exception;
    ResponseEntity<?> getAllFiles(String token) throws IOException;
}
