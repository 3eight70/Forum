package com.hits.common.Client;

import com.hits.common.Models.User.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.hits.common.Consts.DOWNLOAD_FILE;
import static com.hits.common.Consts.UPLOAD_FILE;

@FeignClient(name = "FILE-SERVICE")
public interface FileAppClient {
    @PostMapping(UPLOAD_FILE)
    ResponseEntity<?> uploadFile(UserDto user, @RequestParam("file") MultipartFile file);

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
    ResponseEntity<?> downloadFile(UserDto user, @PathVariable("filename") UUID fileId);


}
