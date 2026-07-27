package com.techknife.finance.service;

import com.techknife.finance.dto.ChartOfAccountDTO;

import java.util.List;

public interface ChartOfAccountService {

    List<ChartOfAccountDTO> getAllAccounts();

    List<ChartOfAccountDTO> getAccountsByType(String accountType);

    ChartOfAccountDTO getAccountById(String id);

    ChartOfAccountDTO createAccount(ChartOfAccountDTO dto);

    ChartOfAccountDTO updateAccount(String id, ChartOfAccountDTO dto);

    void deleteAccount(String id);
}
