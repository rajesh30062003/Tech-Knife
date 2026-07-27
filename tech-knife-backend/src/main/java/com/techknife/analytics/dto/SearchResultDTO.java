package com.techknife.analytics.dto;

import com.techknife.analytics.entity.SearchEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {

    private String id;
    private String entityId;
    private SearchEntityType entityType;
    private String title;
    private String description;
    private String category;
    private String targetUrl;
    private Map<String, Object> metadata;
}
