package com.techknife.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationResponse {
    private int totalRecordsProcessed;
    private int successCount;
    private int failureCount;

    @Builder.Default
    private List<BulkRowError> errors = new ArrayList<>();

    private String message;
}
