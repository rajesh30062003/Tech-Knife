package com.techknife.organization.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.BranchRequest;
import com.techknife.organization.dto.BranchResponse;

import java.util.List;

public interface BranchService {
    BranchResponse createBranch(BranchRequest request);
    BranchResponse updateBranch(String id, BranchRequest request);
    BranchResponse getBranchById(String id);
    BranchResponse getBranchByCode(String code);
    PagedResponse<BranchResponse> getAllBranches(int page, int size, String companyId);
    List<BranchResponse> getBranchesByCompany(String companyId);
    void deleteBranch(String id);
}
