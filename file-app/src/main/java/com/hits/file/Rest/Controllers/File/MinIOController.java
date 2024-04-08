package com.hits.file.Rest.Controllers.File;


import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.file.Core.File.Service.IMinIOService;
import com.hits.security.Rest.Client.FileAppClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Файл", description = "Позволяет работать с файловой системой")
public class MinIOController implements FileAppClient {
    private final IMinIOService minIOService;

    @PostMapping(value = UPLOAD_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Загрузка файла",
            description = "Позволяет загрузить файл, прикрепленный к сообщению, в файловое хранилище"
    )
    @SecurityRequirement(name = "bearerAuth")
    public UUID uploadFile(
            @RequestPart("messageId") @Parameter(description = "Идентификатор сообщения") String messageId,
            @RequestPart("fileId") @Parameter(description = "Идентификатор файла") String fileId,
            @RequestPart("file") MultipartFile file)
    throws IOException, BadRequestException {
        return minIOService.uploadFile(UUID.fromString(messageId), file, UUID.fromString(fileId));
    }

    @PostMapping(DOWNLOAD_FILE + "/{fileId}")
    @Operation(
            summary = "Скачивание файла",
            description = "Позволяет скачать файл"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> downloadFile(
            @AuthenticationPrincipal UserDto user,
            @PathVariable("fileId") @Parameter(description = "Идентификатор файла") UUID fileId)
    throws NotFoundException, IOException{
        return minIOService.downloadFile(fileId);
    }

    @GetMapping(GET_FILES)
    @Operation(
            summary = "Получение файлов, прикрепленных к сообщению",
            description = "Позволяет получить файлы, прикрепленные к сообщению"
    )
    public ResponseEntity<?> getFiles(
            @RequestParam("messageId") @Parameter(description = "Идентификатор сообщения") UUID messageId
    ) throws IOException{
       return minIOService.getAllFiles(messageId);
    }

    @DeleteMapping(DELETE_FILE)
    @Operation(
            summary = "Удаление файла",
            description = "Позволяет удалить файл"
    )
    public ResponseEntity<?> deleteFile(
            @AuthenticationPrincipal UserDto user,
            @RequestParam("messageId") @Parameter(description = "Идентификатор сообщения") UUID messageId,
            @RequestParam("fileId") @Parameter(description = "Идентификатор файла") UUID fileId
    ) throws IOException {
        return minIOService.deleteFile(user, messageId, fileId);
    }
}
