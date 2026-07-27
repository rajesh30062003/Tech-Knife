package com.techknife.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniversalSearchResponse {

    private String query;
    private long totalResults;
    private long executionTimeMs;
    private List<SearchResultDTO> results;
}
