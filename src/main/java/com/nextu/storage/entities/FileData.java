package com.nextu.storage.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class FileData {
    @Id
    private String id;
    private String label;
    private String extension;
    private String createdAt;

    @Version
    private Long version;
}
