package com.hits.file.Controllers;


import com.hits.security.Client.FileAppClient;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.User.UserDto;
import com.hits.file.Services.IMinIOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MinIOController implements FileAppClient {
    private final IMinIOService minIOService;

    @PostMapping(value = UPLOAD_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UUID uploadFile(
            @RequestPart("messageId") String messageId,
            @RequestPart("file") MultipartFile file)
    throws IOException, BadRequestException {
        return minIOService.uploadFile(UUID.fromString(messageId), file);
    }

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
    public ResponseEntity<?> downloadFile(
            @AuthenticationPrincipal UserDto user,
            @PathVariable("filename") UUID fileId)
    throws NotFoundException {
        return minIOService.downloadFile(fileId);
    }

    @GetMapping(GET_FILES)
    public ResponseEntity<?> getFiles(UUID messageId) throws IOException{
       return minIOService.getAllFiles(messageId);
    }

//    @GetMapping(GET_FILE_INFO)
//    public File getFileInfo
}
