package com.techknife.finance.service.impl;

import com.techknife.finance.dto.ChartOfAccountDTO;
import com.techknife.finance.entity.ChartOfAccount;
import com.techknife.finance.repository.ChartOfAccountRepository;
import com.techknife.finance.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartOfAccountServiceImpl implements ChartOfAccountService {

    private final ChartOfAccountRepository chartOfAccountRepository;

    @Override
    public List<ChartOfAccountDTO> getAllAccounts() {
        return chartOfAccountRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartOfAccountDTO> getAccountsByType(String accountType) {
        return chartOfAccountRepository.findByAccountType(accountType.toUpperCase()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChartOfAccountDTO getAccountById(String id) {
        ChartOfAccount account = chartOfAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chart of Account not found with id: " + id));
        return mapToDTO(account);
    }

    @Override
    public ChartOfAccountDTO createAccount(ChartOfAccountDTO dto) {
        if (chartOfAccountRepository.existsByAccountCode(dto.getAccountCode())) {
            throw new IllegalArgumentException("Account code already exists: " + dto.getAccountCode());
        }

        BigDecimal openingBal = dto.getOpeningBalance() != null ? dto.getOpeningBalance() : BigDecimal.ZERO;
        ChartOfAccount account = ChartOfAccount.builder()
                .accountCode(dto.getAccountCode())
                .accountName(dto.getAccountName())
                .accountType(dto.getAccountType().toUpperCase())
                .accountGroupId(dto.getAccountGroupId())
                .parentAccountId(dto.getParentAccountId())
                .isSubAccount(dto.getIsSubAccount() != null ? dto.getIsSubAccount() : false)
                .openingBalance(openingBal)
                .currentBalance(openingBal)
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "INR")
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        ChartOfAccount saved = chartOfAccountRepository.save(account);
        return mapToDTO(saved);
    }

    @Override
    public ChartOfAccountDTO updateAccount(String id, ChartOfAccountDTO dto) {
        ChartOfAccount account = chartOfAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chart of Account not found with id: " + id));

        if (dto.getAccountName() != null) account.setAccountName(dto.getAccountName());
        if (dto.getAccountType() != null) account.setAccountType(dto.getAccountType().toUpperCase());
        if (dto.getAccountGroupId() != null) account.setAccountGroupId(dto.getAccountGroupId());
        if (dto.getParentAccountId() != null) account.setParentAccountId(dto.getParentAccountId());
        if (dto.getIsSubAccount() != null) account.setIsSubAccount(dto.getIsSubAccount());
        if (dto.getOpeningBalance() != null) account.setOpeningBalance(dto.getOpeningBalance());
        if (dto.getCurrency() != null) account.setCurrency(dto.getCurrency());
        if (dto.getDescription() != null) account.setDescription(dto.getDescription());
        if (dto.getStatus() != null) account.setStatus(dto.getStatus());

        ChartOfAccount saved = chartOfAccountRepository.save(account);
        return mapToDTO(saved);
    }

    @Override
    public void deleteAccount(String id) {
        if (!chartOfAccountRepository.existsById(id)) {
            throw new IllegalArgumentException("Chart of Account not found with id: " + id);
        }
        chartOfAccountRepository.deleteById(id);
    }

    private ChartOfAccountDTO mapToDTO(ChartOfAccount acc) {
        return ChartOfAccountDTO.builder()
                .id(acc.getId())
                .accountCode(acc.getAccountCode())
                .accountName(acc.getAccountName())
                .accountType(acc.getAccountType())
                .accountGroupId(acc.getAccountGroupId())
                .parentAccountId(acc.getParentAccountId())
                .isSubAccount(acc.getIsSubAccount())
                .openingBalance(acc.getOpeningBalance())
                .currentBalance(acc.getCurrentBalance())
                .currency(acc.getCurrency())
                .description(acc.getDescription())
                .status(acc.getStatus())
                .createdAt(acc.getCreatedAt())
                .updatedAt(acc.getUpdatedAt())
                .createdBy(acc.getCreatedBy())
                .updatedBy(acc.getUpdatedBy())
                .build();
    }
}
