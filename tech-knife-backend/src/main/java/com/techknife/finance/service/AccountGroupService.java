package com.techknife.finance.service;

import com.techknife.finance.dto.AccountGroupDTO;

import java.util.List;

public interface AccountGroupService {

    List<AccountGroupDTO> getAllAccountGroups();

    List<AccountGroupDTO> getAccountGroupsByType(String accountType);

    AccountGroupDTO getAccountGroupById(String id);

    AccountGroupDTO createAccountGroup(AccountGroupDTO dto);

    AccountGroupDTO updateAccountGroup(String id, AccountGroupDTO dto);

    void deleteAccountGroup(String id);
}
