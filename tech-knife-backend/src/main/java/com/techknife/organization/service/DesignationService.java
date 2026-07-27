package com.techknife.organization.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.DesignationRequest;
import com.techknife.organization.dto.DesignationResponse;

import java.util.List;

public interface DesignationService {
    DesignationResponse createDesignation(DesignationRequest request);
    DesignationResponse updateDesignation(String id, DesignationRequest request);
    DesignationResponse getDesignationById(String id);
    DesignationResponse getDesignationByCode(String code);
    PagedResponse<DesignationResponse> getAllDesignations(int page, int size);
    List<DesignationResponse> getDesignationsByDepartment(String departmentId);
    void deleteDesignation(String id);
}
