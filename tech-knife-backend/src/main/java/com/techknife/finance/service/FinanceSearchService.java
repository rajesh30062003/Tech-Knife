package com.techknife.finance.service;

import com.techknife.finance.dto.FinanceSearchResultDTO;

public interface FinanceSearchService {

    FinanceSearchResultDTO searchFinanceRecords(String query);
}
