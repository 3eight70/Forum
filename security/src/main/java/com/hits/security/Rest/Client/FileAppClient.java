package com.hits.security.Rest.Client;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.security.Rest.Configurations.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Core.Consts.DOWNLOAD_FILE;
import static com.hits.common.Core.Consts.UPLOAD_FILE;

@FeignClient(name = "FILE-SERVICE", configuration = FeignClientConfiguration.class)
public interface FileAppClient {
    @PostMapping(value = UPLOAD_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UUID uploadFile(@RequestPart("messageId") String messageId,
                    @RequestPart("file") MultipartFile file) throws IOException;

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
    ResponseEntity<?> downloadFile(UserDto user, @PathVariable("filename") UUID fileId);
}
