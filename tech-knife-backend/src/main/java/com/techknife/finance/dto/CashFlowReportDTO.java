package com.techknife.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowReportDTO {

    private String financialYearId;

    private BigDecimal operatingCashInflow;

    private BigDecimal operatingCashOutflow;

    private BigDecimal netOperatingCashFlow;

    private BigDecimal netCashFlow;

    private BigDecimal openingCashBalance;

    private BigDecimal closingCashBalance;
}
