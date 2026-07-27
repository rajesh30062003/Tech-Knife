package com.techknife.analytics.dto;

import com.techknife.analytics.entity.KPICategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIHistoryDTO {

    private String id;
    private String kpiId;
    private String kpiCode;
    private KPICategory category;
    private Object value;
    private Object targetValue;
    private Instant recordedAt;
    private Instant createdAt;
}
