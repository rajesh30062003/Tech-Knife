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
public class BulkImportResult {
    private int totalProcessed;
    private int successCount;
    private int failureCount;

    @Builder.Default
    private List<BulkImportError> errors = new ArrayList<>();
}
