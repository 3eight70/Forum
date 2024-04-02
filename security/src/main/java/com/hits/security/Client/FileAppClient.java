package com.hits.security.Client;

import com.hits.common.Models.User.UserDto;
import com.hits.security.Configurations.FeignClientConfiguration;
import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Consts.DOWNLOAD_FILE;
import static com.hits.common.Consts.UPLOAD_FILE;

@FeignClient(name = "FILE-SERVICE", configuration = FeignClientConfiguration.class)
public interface FileAppClient {
    @PostMapping(value = UPLOAD_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UUID uploadFile(@RequestPart("messageId") String messageId,
                    @RequestPart("file") MultipartFile file) throws IOException;

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
    ResponseEntity<?> downloadFile(UserDto user, @PathVariable("filename") UUID fileId);
}
