package com.techknife.finance.service.impl;

import com.techknife.finance.dto.AccountGroupDTO;
import com.techknife.finance.entity.AccountGroup;
import com.techknife.finance.repository.AccountGroupRepository;
import com.techknife.finance.service.AccountGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountGroupServiceImpl implements AccountGroupService {

    private final AccountGroupRepository accountGroupRepository;

    @Override
    public List<AccountGroupDTO> getAllAccountGroups() {
        return accountGroupRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountGroupDTO> getAccountGroupsByType(String accountType) {
        return accountGroupRepository.findByAccountType(accountType.toUpperCase()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountGroupDTO getAccountGroupById(String id) {
        AccountGroup group = accountGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account group not found with id: " + id));
        return mapToDTO(group);
    }

    @Override
    public AccountGroupDTO createAccountGroup(AccountGroupDTO dto) {
        if (accountGroupRepository.existsByGroupCode(dto.getGroupCode())) {
            throw new IllegalArgumentException("Account group code already exists: " + dto.getGroupCode());
        }

        AccountGroup group = AccountGroup.builder()
                .groupCode(dto.getGroupCode())
                .groupName(dto.getGroupName())
                .accountType(dto.getAccountType().toUpperCase())
                .parentGroupId(dto.getParentGroupId())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        AccountGroup saved = accountGroupRepository.save(group);
        return mapToDTO(saved);
    }

    @Override
    public AccountGroupDTO updateAccountGroup(String id, AccountGroupDTO dto) {
        AccountGroup group = accountGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account group not found with id: " + id));

        if (dto.getGroupName() != null) group.setGroupName(dto.getGroupName());
        if (dto.getAccountType() != null) group.setAccountType(dto.getAccountType().toUpperCase());
        if (dto.getParentGroupId() != null) group.setParentGroupId(dto.getParentGroupId());
        if (dto.getDescription() != null) group.setDescription(dto.getDescription());
        if (dto.getStatus() != null) group.setStatus(dto.getStatus());

        AccountGroup saved = accountGroupRepository.save(group);
        return mapToDTO(saved);
    }

    @Override
    public void deleteAccountGroup(String id) {
        if (!accountGroupRepository.existsById(id)) {
            throw new IllegalArgumentException("Account group not found with id: " + id);
        }
        accountGroupRepository.deleteById(id);
    }

    private AccountGroupDTO mapToDTO(AccountGroup group) {
        return AccountGroupDTO.builder()
                .id(group.getId())
                .groupCode(group.getGroupCode())
                .groupName(group.getGroupName())
                .accountType(group.getAccountType())
                .parentGroupId(group.getParentGroupId())
                .description(group.getDescription())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .createdBy(group.getCreatedBy())
                .updatedBy(group.getUpdatedBy())
                .build();
    }
}
