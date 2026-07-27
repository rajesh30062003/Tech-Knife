package com.techknife.customerportal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFeedbackDTO {

    private String id;
    private String customerAccountId;
    private String customerName;
    private String projectId;
    private String projectName;
    private String employeeId;
    private String employeeName;

    @Min(value = 1, message = "Project rating must be between 1 and 5")
    @Max(value = 5, message = "Project rating must be between 1 and 5")
    private Integer projectRating;

    @Min(value = 1, message = "Employee rating must be between 1 and 5")
    @Max(value = 5, message = "Employee rating must be between 1 and 5")
    private Integer employeeRating;

    private String comments;
    private String suggestions;
    private Double satisfactionScore;
    private Instant createdAt;
}
