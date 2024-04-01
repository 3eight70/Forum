package com.hits.file.Services;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.User.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IMinIOService {
    ResponseEntity<?> uploadFile(UserDto user, MultipartFile file) throws IOException, BadRequestException;
    ResponseEntity<?> downloadFile(UUID id) throws NotFoundException;
    ResponseEntity<?> getAllFiles(UserDto user) throws IOException;
}
