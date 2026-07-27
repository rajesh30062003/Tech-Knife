package com.techknife.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkRowError {
    private int rowNumber;
    private String identifier; // Employee Code or Email
    private String errorMessage;
}
