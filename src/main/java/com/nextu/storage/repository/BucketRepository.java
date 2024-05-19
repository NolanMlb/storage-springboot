package com.nextu.storage.repository;

import com.nextu.storage.entities.Bucket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BucketRepository extends MongoRepository<Bucket,String> {

    Optional<Bucket> findByLabel(String label);
}
