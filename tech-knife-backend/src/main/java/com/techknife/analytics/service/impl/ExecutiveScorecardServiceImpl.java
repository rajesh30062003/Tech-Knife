package com.techknife.analytics.service.impl;

import com.techknife.analytics.dto.ExecutiveScorecardDTO;
import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.entity.ExecutiveScorecard;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.repository.ExecutiveScorecardRepository;
import com.techknife.analytics.service.ExecutiveScorecardService;
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
public class ExecutiveScorecardServiceImpl implements ExecutiveScorecardService {

    private final ExecutiveScorecardRepository scorecardRepository;

    @Override
    public List<ExecutiveScorecardDTO> getAllScorecards() {
        return scorecardRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExecutiveScorecardDTO getScorecardByRoleAndPeriod(ExecutiveRole role, String period) {
        String queryPeriod = (period == null || period.isBlank()) ? "Q3 2026" : period;
        return scorecardRepository.findByRoleAndPeriod(role, queryPeriod)
                .map(this::mapToDTO)
                .orElseGet(() -> generateExecutiveScorecard(role, queryPeriod));
    }

    @Override
    public ExecutiveScorecardDTO generateExecutiveScorecard(ExecutiveRole role, String period) {
        String activePeriod = (period == null || period.isBlank()) ? "Q3 2026" : period;

        List<ExecutiveScorecard.ScorecardMetric> keyMetrics = new ArrayList<>();
        keyMetrics.add(ExecutiveScorecard.ScorecardMetric.builder()
                .metricKey("REV_GROWTH")
                .name("Revenue Growth Rate")
                .category(KPICategory.FINANCE)
                .value("18.5%")
                .target("15.0%")
                .percentageAchieved(123.3)
                .status("EXCEEDED")
                .build());

        keyMetrics.add(ExecutiveScorecard.ScorecardMetric.builder()
                .metricKey("EMP_RETENTION")
                .name("Employee Retention Rate")
                .category(KPICategory.EMPLOYEE)
                .value("94.2%")
                .target("92.0%")
                .percentageAchieved(102.4)
                .status("ON_TRACK")
                .build());

        keyMetrics.add(ExecutiveScorecard.ScorecardMetric.builder()
                .metricKey("PROJECT_DELIVERY")
                .name("On-Time Project Delivery")
                .category(KPICategory.PROJECTS)
                .value("88.0%")
                .target("95.0%")
                .percentageAchieved(92.6)
                .status("AT_RISK")
                .build());

        Map<String, Object> highlights = new HashMap<>();
        highlights.put("topHighlight", "Financial performance exceeded quarterly target by 23.3%");
        highlights.put("riskArea", "Project delivery milestone delays in engineering division");
        highlights.put("actionItem", "Reallocate senior tech leads to critical project path");

        ExecutiveScorecard scorecard = ExecutiveScorecard.builder()
                .role(role != null ? role : ExecutiveRole.CEO)
                .title("Executive Scorecard - " + (role != null ? role.name() : "CEO"))
                .period(activePeriod)
                .overallPerformanceScore(94.8)
                .keyMetrics(keyMetrics)
                .summaryHighlights(highlights)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return mapToDTO(scorecardRepository.save(scorecard));
    }

    private ExecutiveScorecardDTO mapToDTO(ExecutiveScorecard entity) {
        if (entity == null) return null;
        return ExecutiveScorecardDTO.builder()
                .id(entity.getId())
                .role(entity.getRole())
                .title(entity.getTitle())
                .period(entity.getPeriod())
                .overallPerformanceScore(entity.getOverallPerformanceScore())
                .keyMetrics(entity.getKeyMetrics())
                .summaryHighlights(entity.getSummaryHighlights())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
