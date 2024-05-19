package com.nextu.storage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class BucketDTO {
    private String id;
    private String label;
    private String description;
}
