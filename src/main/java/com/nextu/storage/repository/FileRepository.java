package com.nextu.storage.repository;

import com.nextu.storage.entities.FileData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<FileData,String> {
    FileData findByLabel(String fileName);
    FileData findByCreatedAt(String creationDate);
}
