package com.techknife.analytics.dto;

import com.techknife.analytics.entity.SearchEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniversalSearchRequest {

    private String query;
    private List<SearchEntityType> entityTypes;
    private int limit;
}
