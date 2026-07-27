package com.techknife.organization.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.CompanyRequest;
import com.techknife.organization.dto.CompanyResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse updateCompany(String id, CompanyRequest request);
    CompanyResponse getCompanyById(String id);
    CompanyResponse getCompanyByCode(String code);
    PagedResponse<CompanyResponse> getAllCompanies(int page, int size, String search);
    List<CompanyResponse> getAllActiveCompanies();
    void deleteCompany(String id);
}
