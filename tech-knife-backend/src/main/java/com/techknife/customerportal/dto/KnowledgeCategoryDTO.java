package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeCategoryDTO {

    private String id;
    private String categoryName;
    private String description;
    private String icon;
    private Integer articleCount;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}
