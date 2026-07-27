package com.techknife.analytics.dto;

import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.entity.ExecutiveScorecard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutiveScorecardDTO {

    private String id;
    private ExecutiveRole role;
    private String title;
    private String period;
    private Double overallPerformanceScore;
    
    private List<ExecutiveScorecard.ScorecardMetric> keyMetrics;
    private Map<String, Object> summaryHighlights;
    private Instant createdAt;
    private Instant updatedAt;
}
