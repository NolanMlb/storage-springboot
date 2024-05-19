package com.nextu.storage.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document
@NoArgsConstructor
@Data
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    @DocumentReference
    private List<Bucket> buckets;

    public void addBucket(Bucket bucket){
        if(this.buckets==null){
            this.buckets = new ArrayList<>();
        }
        this.buckets.add(bucket);
    }
}
