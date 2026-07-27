package com.techknife.organization.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.DepartmentRequest;
import com.techknife.organization.dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(String id, DepartmentRequest request);
    DepartmentResponse getDepartmentById(String id);
    DepartmentResponse getDepartmentByCode(String code);
    PagedResponse<DepartmentResponse> getAllDepartments(int page, int size, String companyId);
    List<DepartmentResponse> getDepartmentsByCompany(String companyId);
    List<DepartmentResponse> getDepartmentsByBranch(String branchId);
    void deleteDepartment(String id);
}
