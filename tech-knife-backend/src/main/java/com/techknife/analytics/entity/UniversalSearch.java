package com.techknife.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_universal_searches")
public class UniversalSearch {

    @Id
    private String id;

    private String query;
    private List<SearchEntityType> entityTypes;
    private long totalResultsFound;
    private long executionTimeMs;

    @CreatedDate
    private Instant searchedAt;

    @CreatedBy
    private String searchedBy;
}
