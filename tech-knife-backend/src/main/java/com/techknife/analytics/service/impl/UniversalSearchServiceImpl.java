package com.techknife.analytics.service.impl;

import com.techknife.analytics.dto.*;
import com.techknife.analytics.entity.SearchEntityType;
import com.techknife.analytics.entity.SearchIndex;
import com.techknife.analytics.entity.UniversalSearch;
import com.techknife.analytics.repository.SearchIndexRepository;
import com.techknife.analytics.repository.UniversalSearchRepository;
import com.techknife.analytics.service.UniversalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversalSearchServiceImpl implements UniversalSearchService {

    private final SearchIndexRepository searchIndexRepository;
    private final UniversalSearchRepository universalSearchRepository;

    @Override
    public UniversalSearchResponse search(UniversalSearchRequest request, String searchedBy) {
        long startTime = System.currentTimeMillis();
        String query = request.getQuery() != null ? request.getQuery().trim() : "";
        List<SearchEntityType> types = request.getEntityTypes();

        List<SearchIndex> results;
        if (query.isBlank()) {
            results = searchIndexRepository.findAll();
        } else if (types != null && !types.isEmpty()) {
            results = searchIndexRepository.searchByKeywordAndEntityTypes(query, types);
        } else {
            results = searchIndexRepository.searchByKeyword(query);
        }

        if (results.isEmpty() && searchIndexRepository.count() == 0) {
            // Seed sample search index if empty
            reindexAll();
            if (query.isBlank()) {
                results = searchIndexRepository.findAll();
            } else if (types != null && !types.isEmpty()) {
                results = searchIndexRepository.searchByKeywordAndEntityTypes(query, types);
            } else {
                results = searchIndexRepository.searchByKeyword(query);
            }
        }

        int limit = request.getLimit() > 0 ? request.getLimit() : 20;
        List<SearchResultDTO> searchResults = results.stream()
                .limit(limit)
                .map(this::mapToResultDTO)
                .collect(Collectors.toList());

        long executionTime = System.currentTimeMillis() - startTime;

        UniversalSearch searchAudit = UniversalSearch.builder()
                .query(query)
                .entityTypes(types)
                .totalResultsFound(searchResults.size())
                .executionTimeMs(executionTime)
                .searchedAt(Instant.now())
                .searchedBy(searchedBy != null ? searchedBy : "ANONYMOUS")
                .build();
        universalSearchRepository.save(searchAudit);

        return UniversalSearchResponse.builder()
                .query(query)
                .totalResults(searchResults.size())
                .executionTimeMs(executionTime)
                .results(searchResults)
                .build();
    }

    @Override
    public SearchIndexDTO indexEntity(SearchIndexDTO dto) {
        SearchIndex index = searchIndexRepository.findByEntityIdAndEntityType(dto.getEntityId(), dto.getEntityType())
                .orElseGet(() -> SearchIndex.builder()
                        .entityId(dto.getEntityId())
                        .entityType(dto.getEntityType())
                        .createdAt(Instant.now())
                        .build());

        index.setTitle(dto.getTitle());
        index.setDescription(dto.getDescription());
        index.setCategory(dto.getCategory());
        index.setTags(dto.getTags());
        index.setTargetUrl(dto.getTargetUrl());
        index.setMetadata(dto.getMetadata());
        index.setUpdatedAt(Instant.now());

        SearchIndex saved = searchIndexRepository.save(index);
        return mapToIndexDTO(saved);
    }

    @Override
    public void removeIndex(String entityId, SearchEntityType entityType) {
        searchIndexRepository.deleteByEntityIdAndEntityType(entityId, entityType);
    }

    @Override
    public void reindexAll() {
        // Seed default universal search index across modules
        List<SearchIndex> indices = new ArrayList<>();

        Map<String, Object> m1 = new HashMap<>();
        m1.put("department", "Engineering");
        indices.add(SearchIndex.builder()
                .entityId("EMP-001")
                .entityType(SearchEntityType.EMPLOYEE)
                .title("John Doe - Principal Engineer")
                .description("Employee in Engineering Department")
                .category("EMPLOYEE")
                .tags("employee, engineer, tech, java")
                .targetUrl("/employees/EMP-001")
                .metadata(m1)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Map<String, Object> m2 = new HashMap<>();
        m2.put("accountType", "ENTERPRISE");
        indices.add(SearchIndex.builder()
                .entityId("CUST-101")
                .entityType(SearchEntityType.CUSTOMER)
                .title("Acme Corporation")
                .description("Enterprise Key Customer Account")
                .category("CUSTOMER")
                .tags("customer, enterprise, key account")
                .targetUrl("/customers/CUST-101")
                .metadata(m2)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Map<String, Object> m3 = new HashMap<>();
        m3.put("status", "IN_PROGRESS");
        indices.add(SearchIndex.builder()
                .entityId("PROJ-301")
                .entityType(SearchEntityType.PROJECT)
                .title("Tech Knife Cloud Migration")
                .description("Enterprise Infrastructure Cloud Migration Project")
                .category("PROJECT")
                .tags("project, cloud, infrastructure")
                .targetUrl("/projects/PROJ-301")
                .metadata(m3)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Map<String, Object> m4 = new HashMap<>();
        m4.put("amount", 250000.0);
        indices.add(SearchIndex.builder()
                .entityId("INV-9001")
                .entityType(SearchEntityType.INVOICE)
                .title("Invoice #INV-9001 - Acme Corp")
                .description("Annual Software Maintenance Invoice")
                .category("FINANCE")
                .tags("invoice, finance, payment")
                .targetUrl("/finance/invoices/INV-9001")
                .metadata(m4)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        searchIndexRepository.saveAll(indices);
    }

    private SearchResultDTO mapToResultDTO(SearchIndex index) {
        if (index == null) return null;
        return SearchResultDTO.builder()
                .id(index.getId())
                .entityId(index.getEntityId())
                .entityType(index.getEntityType())
                .title(index.getTitle())
                .description(index.getDescription())
                .category(index.getCategory())
                .targetUrl(index.getTargetUrl())
                .metadata(index.getMetadata())
                .build();
    }

    private SearchIndexDTO mapToIndexDTO(SearchIndex index) {
        if (index == null) return null;
        return SearchIndexDTO.builder()
                .id(index.getId())
                .entityId(index.getEntityId())
                .entityType(index.getEntityType())
                .title(index.getTitle())
                .description(index.getDescription())
                .category(index.getCategory())
                .tags(index.getTags())
                .targetUrl(index.getTargetUrl())
                .metadata(index.getMetadata())
                .build();
    }
}
