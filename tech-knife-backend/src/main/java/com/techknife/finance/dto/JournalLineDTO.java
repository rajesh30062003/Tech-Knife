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
public class JournalLineDTO {

    private String accountId;

    private String accountCode;

    private String accountName;

    private String costCenterId;

    private BigDecimal debitAmount;

    private BigDecimal creditAmount;

    private String narration;
}
