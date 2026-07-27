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
public class KPIGroupDTO {

    private String id;
    private String name;
    private String code;
    private String description;
    private KPICategory category;
    private String icon;
    private int displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
