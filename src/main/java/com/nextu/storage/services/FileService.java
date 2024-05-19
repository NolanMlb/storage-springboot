package com.nextu.storage.services;

import com.nextu.storage.entities.Bucket;
import com.nextu.storage.entities.FileData;
import com.nextu.storage.repository.BucketRepository;
import com.nextu.storage.repository.FileRepository;
import com.nextu.storage.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final BucketRepository bucketRepository;

    public FileData saveFileByBucketId(String bucketId, String fileName, String createdAt) throws Exception {
        Bucket bucket = bucketRepository.findById(bucketId).orElse(null);
        if(bucket!=null){
            FileData file = new FileData();
            file.setLabel(fileName.split("\\.")[0]);
            String extension = FileUtils.getExtension(fileName);
            file.setCreatedAt(createdAt);
            file.setExtension(extension);
            FileData fileSaved = fileRepository.save(file);
            bucket.addFile(fileSaved);
            bucketRepository.save(bucket);
            return fileSaved;
        }else{
            throw new Exception("save file for the current user id"+bucketId+" encountered an error");
        }
    }
}
