package com.nextu.storage.controllers;

import com.nextu.storage.entities.Bucket;
import com.nextu.storage.entities.FileData;
import com.nextu.storage.repository.BucketRepository;
import com.nextu.storage.repository.FileRepository;
import com.nextu.storage.services.FileService;
import com.nextu.storage.services.StorageService;
import com.nextu.storage.utils.FileUtils;
import com.nextu.storage.utils.MimeTypeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Slf4j
public class FileController {
    private final FileRepository fileRepository;
    private final StorageService storageService;

    @GetMapping(value = "/{fileId}")
    public ResponseEntity<?> find(@PathVariable String fileId){
        Optional<FileData> fileData = fileRepository.findById(fileId);
        String fileNameInFolder = fileData.get().getCreatedAt() + '_' + fileId + '.' + fileData.get().getExtension();
        try {
            File file = this.storageService.load(fileNameInFolder);
            var extension = FileUtils.getExtension(file.getName());
            Path path = Paths.get(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity
                    .ok()
                    .contentLength(path.toFile().length())
                    .contentType(MediaType.parseMediaType(MimeTypeUtils.getMimeType(extension)))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
