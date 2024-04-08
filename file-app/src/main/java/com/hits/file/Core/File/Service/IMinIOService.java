package com.hits.file.Core.File.Service;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IMinIOService {
    UUID uploadFile(UUID messageId, MultipartFile file, UUID fileId) throws IOException, BadRequestException;
    ResponseEntity<?> downloadFile(UUID id) throws NotFoundException, IOException;
    ResponseEntity<?> getAllFiles(UUID messageId) throws IOException;
    ResponseEntity<?> deleteFile(UserDto user, UUID messageId, UUID fileId) throws IOException;
}
