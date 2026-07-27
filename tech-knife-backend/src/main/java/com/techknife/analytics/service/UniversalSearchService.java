package com.techknife.analytics.service;

import com.techknife.analytics.dto.SearchIndexDTO;
import com.techknife.analytics.dto.UniversalSearchRequest;
import com.techknife.analytics.dto.UniversalSearchResponse;
import com.techknife.analytics.entity.SearchEntityType;

public interface UniversalSearchService {
    UniversalSearchResponse search(UniversalSearchRequest request, String searchedBy);
    SearchIndexDTO indexEntity(SearchIndexDTO indexDTO);
    void removeIndex(String entityId, SearchEntityType entityType);
    void reindexAll();
}
