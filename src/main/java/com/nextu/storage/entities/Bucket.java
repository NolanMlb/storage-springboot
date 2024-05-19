package com.nextu.storage.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Bucket {
    @Id
    private String id;
    private String label;
    private String description;
    private OffsetDateTime dateCreated;

    @Version
    private Long version;

    @DocumentReference
    private List<FileData> files;

    public void addFile(FileData file){
        if(this.files==null){
            this.files = new ArrayList<>();
        }
        this.files.add(file);
    }

    public void removeFile(FileData file){
        if(this.files!=null){
            this.files.remove(file);
        }
    }
}
