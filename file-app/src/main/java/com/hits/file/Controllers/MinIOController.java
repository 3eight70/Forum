package com.hits.file.Controllers;


import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.User.UserDto;
import com.hits.file.Services.IMinIOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class MinIOController {
    private final IMinIOService minIOService;

    @PostMapping(UPLOAD_FILE)
    public ResponseEntity<?> uploadFile(
            @AuthenticationPrincipal UserDto user,
            @RequestParam("file") MultipartFile file)
    throws IOException, BadRequestException {
        return minIOService.uploadFile(user, file);
    }

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
    public ResponseEntity<?> downloadFile(
            @AuthenticationPrincipal UserDto user,
            @PathVariable("filename") UUID fileId)
    throws NotFoundException {
        return minIOService.downloadFile(user, fileId);
    }

    @GetMapping(GET_FILES)
    public ResponseEntity<?> getFiles(@AuthenticationPrincipal UserDto user) throws IOException{
       return minIOService.getAllFiles(user);
    }
}
