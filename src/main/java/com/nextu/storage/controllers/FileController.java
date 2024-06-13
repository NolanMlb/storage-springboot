package com.nextu.storage.controllers;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import com.nextu.storage.entities.FileData;
import com.nextu.storage.repository.FileRepository;
import com.nextu.storage.services.StorageService;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.Optional;
import com.nextu.storage.utils.MimeTypeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Slf4j
public class FileController {
    private final FileRepository fileRepository;
    @Autowired
    private AmazonS3 amazonS3;
    @Value("${s3.bucket_name}")
    private String bucketName;

    @GetMapping(value = "/{fileId}")
    public ResponseEntity<?> find(@PathVariable String fileId) {
        Optional<FileData> fileDataOpt = fileRepository.findById(fileId);
        if (!fileDataOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }

        FileData fileData = fileDataOpt.get();
        String fileNameInFolder = fileData.getCreatedAt() + '_' + fileId + '.' + fileData.getExtension();

        try {
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileNameInFolder));
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity
                    .ok()
                    .contentLength(bytes.length)
                    .contentType(MediaType.parseMediaType(MimeTypeUtils.getMimeType(fileData.getExtension())))
                    .body(resource);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching the file from S3");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while reading the file content");
        }
    }
}
