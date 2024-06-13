package com.nextu.storage.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nextu.storage.exceptions.FileContentException;
import com.nextu.storage.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    @Value("${nextu.filestore}")
    private String SERVER_LOCATION;
    private Path root;
    private final Logger logger = LoggerFactory.getLogger(StorageService.class);
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${s3.bucket_name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            this.root = Paths.get(SERVER_LOCATION);
            Files.createDirectory(root);
        } catch (IOException e) {
            logger.warn("folder upload exits");
        }
    }

    public void save(MultipartFile file, String fileNameInFolder) throws FileContentException {
        copyFile(file, fileNameInFolder);
    }

    public String uploadFile(MultipartFile file, String fileName) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void copyFile(MultipartFile file, String fileNameInFolder) throws FileContentException {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(fileNameInFolder));
        } catch (Exception e) {
            logger.error("exception happened when saving file {}",e.getMessage());
            throw new FileContentException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void delete(String fileName) throws IOException {
        Files.delete(this.root.resolve(fileName));
    }

    public File load(String filename) throws IOException {
        return new File(SERVER_LOCATION + filename);
    }
}
