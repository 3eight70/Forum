package com.hits.file.Services;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IMinIOService {
    UUID uploadFile(UUID messageId, MultipartFile file) throws IOException, BadRequestException;
    ResponseEntity<?> downloadFile(UUID id) throws NotFoundException;
    ResponseEntity<?> getAllFiles(UUID messageId) throws IOException;
}
