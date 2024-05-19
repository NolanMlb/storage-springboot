package com.nextu.storage.services;

import com.nextu.storage.dto.BucketDTO;
import com.nextu.storage.entities.Bucket;
import com.nextu.storage.entities.FileData;
import com.nextu.storage.entities.User;
import com.nextu.storage.repository.BucketRepository;
import com.nextu.storage.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@RequiredArgsConstructor
@Slf4j
@Service
public class BucketService {
    private final BucketRepository bucketRepository;

    public BucketDTO create(BucketDTO bucketDTO) {
        Bucket bucket = new Bucket();
        bucket.setLabel(bucketDTO.getLabel());
        bucket.setDescription(bucketDTO.getDescription());
        Bucket bucketAfterSave = bucketRepository.save(bucket);
        bucketDTO.setId(bucketAfterSave.getId());
        return bucketDTO;
    }

    public Bucket findById(String id) {
        return bucketRepository.findById(id).orElseGet(null);
    }
}
