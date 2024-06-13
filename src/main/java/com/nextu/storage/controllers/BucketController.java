package com.nextu.storage.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.nextu.storage.dto.BucketDTO;
import com.nextu.storage.entities.FileData;
import com.nextu.storage.entities.User;
import com.nextu.storage.repository.BucketRepository;
import com.nextu.storage.repository.FileRepository;
import com.nextu.storage.services.*;
import com.nextu.storage.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.nextu.storage.entities.Bucket;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/buckets")
@Slf4j
public class BucketController {
    private final BucketService bucketService;
    private final BucketRepository bucketRepository;
    private final UserService userService;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final StorageService storageService;
    @Autowired
    private AmazonS3 amazonS3;
    @Value("${s3.bucket_name}")
    private String bucketName;

    @PostMapping(value = "/", produces = { "application/json", "application/xml" })
    public ResponseEntity<?> create(@RequestBody BucketDTO bucketDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findUserById(userDetails.getId());
        bucketService.create(bucketDTO);
        Bucket bucket = bucketService.findById(bucketDTO.getId());
        user.addBucket(bucket);
        userService.update(user);
        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "/{idBucket}", produces = { "application/json", "application/xml" })
    public ResponseEntity<Bucket> getFilesByIdBucket(@PathVariable String idBucket) {
        return ResponseEntity.ok(bucketService.findById(idBucket));
    }

    @GetMapping(value = "/", produces = { "application/json", "application/xml" })
    public ResponseEntity<List<Bucket>> getBuckets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findUserById(userDetails.getId());
        return ResponseEntity.ok(user.getBuckets());
    }

    @PostMapping(value = "/{id}/files")
    public ResponseEntity<List<FileData>> saveFiles(@PathVariable String id, @RequestParam("files") MultipartFile[] files) {
        List<FileData> filesData = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                var fileNameInFolder = FileUtils.generateStringFromDate(FileUtils.getExtension(file.getOriginalFilename()));
                String createdAt = fileNameInFolder.split("\\.")[0];
                String fileName = file.getOriginalFilename();
                FileData fileData = fileService.saveFileByBucketId(id, fileName, createdAt);
                filesData.add(fileData);
                fileNameInFolder = createdAt + '_' + fileData.getId() + "." + FileUtils.getExtension(fileName);
                storageService.uploadFile(file, fileNameInFolder);
            }
            return ResponseEntity.ok(filesData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @PutMapping(value = "/{bucketId}", produces = { "application/json", "application/xml" })
    public ResponseEntity<?> update(@RequestBody BucketDTO bucketDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findUserById(userDetails.getId());
        bucketService.create(bucketDTO);
        Bucket bucket = bucketService.findById(bucketDTO.getId());
        user.addBucket(bucket);
        userService.update(user);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping(value = "/{bucketId}/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String bucketId, @PathVariable String fileId) {
        try {
            Optional<FileData> fileOpt = fileRepository.findById(fileId);
            if (fileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            FileData file = fileOpt.get();
            String fileName = file.getCreatedAt() + "_" + fileId + "." + file.getExtension();

            // Delete the file from S3
            amazonS3.deleteObject(bucketName, fileName);

            // Remove the file from the repository
            fileRepository.deleteById(fileId);

            // Update the bucket
            Bucket bucket = bucketService.findById(bucketId);
            bucket.removeFile(file);
            bucketRepository.save(bucket);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
