package com.hits.security.Rest.Client;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.security.Rest.Configurations.FeignClientConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@FeignClient(name = "FILE-SERVICE", configuration = FeignClientConfiguration.class)
public interface FileAppClient {
    @PostMapping(value = UPLOAD_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UUID uploadFile(@RequestPart("messageId") String messageId,
                    @RequestPart("fileId") String fileId,
                    @RequestPart("file") MultipartFile file) throws IOException;

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
    ResponseEntity<?> downloadFile(UserDto user, @PathVariable("filename") UUID fileId) throws NotFoundException, IOException;

    @DeleteMapping(DELETE_FILE)
    ResponseEntity<?> deleteFile(
            UserDto user,
            @RequestParam("messageId") UUID messageId,
            @RequestParam("fileId") UUID fileId
    ) throws IOException;
}
