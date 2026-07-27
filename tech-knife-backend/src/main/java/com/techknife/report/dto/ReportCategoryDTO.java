package com.techknife.report.dto;

import com.techknife.report.entity.ReportCategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCategoryDTO {
    private String id;

    @NotNull(message = "Category type is required")
    private ReportCategoryType categoryType;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private String icon;
    private int displayOrder;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
