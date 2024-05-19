package com.nextu.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nextu.storage.entities.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByLogin(String login);

}
