package com.techknife.analytics.service.impl;

import com.techknife.analytics.dto.BusinessInsightDTO;
import com.techknife.analytics.entity.BusinessInsight;
import com.techknife.analytics.entity.InsightSeverity;
import com.techknife.analytics.entity.InsightType;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.repository.BusinessInsightRepository;
import com.techknife.analytics.service.BusinessInsightService;
import com.techknife.common.exception.ResourceNotFoundException;
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
public class BusinessInsightServiceImpl implements BusinessInsightService {

    private final BusinessInsightRepository businessInsightRepository;

    @Override
    public List<BusinessInsightDTO> getAllInsights() {
        List<BusinessInsight> list = businessInsightRepository.findAll();
        if (list.isEmpty()) {
            return generateInsights();
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BusinessInsightDTO> getInsightsBySeverity(InsightSeverity severity) {
        return businessInsightRepository.findBySeverity(severity).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusinessInsightDTO> getInsightsByCategory(KPICategory category) {
        return businessInsightRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BusinessInsightDTO acknowledgeInsight(String id) {
        BusinessInsight insight = businessInsightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business insight not found with id: " + id));

        insight.setAcknowledged(true);
        return mapToDTO(businessInsightRepository.save(insight));
    }

    @Override
    public List<BusinessInsightDTO> generateInsights() {
        List<BusinessInsight> insights = new ArrayList<>();

        Map<String, Object> m1 = new HashMap<>();
        m1.put("quarterlyRevenue", "$1,450,000");
        m1.put("growthRate", "+24%");
        insights.add(BusinessInsight.builder()
                .insightType(InsightType.TOP_PERFORMING_DEPARTMENT)
                .severity(InsightSeverity.SUCCESS)
                .category(KPICategory.SALES)
                .title("Enterprise Sales Department Leading Growth")
                .description("Enterprise Sales generated 24% higher revenue than initial projections for the quarter.")
                .recommendation("Increase sales team resource budget by 10% to capture additional enterprise demand.")
                .impactScore(92.0)
                .targetEntityName("Enterprise Sales")
                .metrics(m1)
                .acknowledged(false)
                .createdAt(Instant.now())
                .build());

        Map<String, Object> m2 = new HashMap<>();
        m2.put("averageDaysToHire", 45);
        m2.put("targetDaysToHire", 25);
        insights.add(BusinessInsight.builder()
                .insightType(InsightType.RECRUITMENT_BOTTLENECK)
                .severity(InsightSeverity.WARNING)
                .category(KPICategory.RECRUITMENT)
                .title("Recruitment Bottleneck in Senior Engineering Roles")
                .description("Engineering candidate interview pipeline is averaging 45 days, exceeding the 25-day target.")
                .recommendation("Streamline technical screening rounds from 4 to 2 stages.")
                .impactScore(78.5)
                .targetEntityName("Engineering Talent Acquisition")
                .metrics(m2)
                .acknowledged(false)
                .createdAt(Instant.now())
                .build());

        Map<String, Object> m3 = new HashMap<>();
        m3.put("stockCount", 12);
        m3.put("reorderThreshold", 50);
        insights.add(BusinessInsight.builder()
                .insightType(InsightType.LOW_INVENTORY_ALERT)
                .severity(InsightSeverity.CRITICAL)
                .category(KPICategory.INVENTORY)
                .title("Critical Low Inventory Alert: High-Grade Blade Assets")
                .description("Current stock level for High-Grade Blade units dropped below safe reorder threshold.")
                .recommendation("Trigger emergency procurement purchase order to primary supplier.")
                .impactScore(95.0)
                .targetEntityName("Blade SKU-9021")
                .metrics(m3)
                .acknowledged(false)
                .createdAt(Instant.now())
                .build());

        List<BusinessInsight> savedList = businessInsightRepository.saveAll(insights);
        return savedList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private BusinessInsightDTO mapToDTO(BusinessInsight entity) {
        if (entity == null) return null;
        return BusinessInsightDTO.builder()
                .id(entity.getId())
                .insightType(entity.getInsightType())
                .severity(entity.getSeverity())
                .category(entity.getCategory())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .recommendation(entity.getRecommendation())
                .impactScore(entity.getImpactScore())
                .targetEntityId(entity.getTargetEntityId())
                .targetEntityName(entity.getTargetEntityName())
                .metrics(entity.getMetrics())
                .acknowledged(entity.isAcknowledged())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
