package com.techknife.organization.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.organization.dto.DesignationRequest;
import com.techknife.organization.dto.DesignationResponse;
import com.techknife.organization.entity.Designation;
import com.techknife.organization.entity.OrganizationStatus;
import com.techknife.organization.repository.DepartmentRepository;
import com.techknife.organization.repository.DesignationRepository;
import com.techknife.organization.service.DesignationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public DesignationResponse createDesignation(DesignationRequest request) {
        if (designationRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Designation code already exists: " + request.getCode());
        }

        if (request.getDepartmentId() != null && !request.getDepartmentId().trim().isEmpty()) {
            if (!departmentRepository.existsById(request.getDepartmentId())) {
                throw new ResourceNotFoundException("Department", "id", request.getDepartmentId());
            }
        }

        Designation designation = Designation.builder()
                .companyId(request.getCompanyId())
                .departmentId(request.getDepartmentId())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .level(request.getLevel())
                .status(request.getStatus() != null ? request.getStatus() : OrganizationStatus.ACTIVE)
                .build();

        Designation saved = designationRepository.save(designation);
        log.info("Created designation ID: {} with code: {}", saved.getId(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    public DesignationResponse updateDesignation(String id, DesignationRequest request) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "id", id));

        if (!designation.getCode().equals(request.getCode()) && designationRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Designation code already exists: " + request.getCode());
        }

        if (request.getDepartmentId() != null && !departmentRepository.existsById(request.getDepartmentId())) {
            throw new ResourceNotFoundException("Department", "id", request.getDepartmentId());
        }

        designation.setCompanyId(request.getCompanyId());
        designation.setDepartmentId(request.getDepartmentId());
        designation.setCode(request.getCode());
        designation.setName(request.getName());
        designation.setDescription(request.getDescription());
        designation.setLevel(request.getLevel());
        if (request.getStatus() != null) {
            designation.setStatus(request.getStatus());
        }

        Designation updated = designationRepository.save(designation);
        log.info("Updated designation ID: {}", id);
        return mapToResponse(updated);
    }

    @Override
    public DesignationResponse getDesignationById(String id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "id", id));
        return mapToResponse(designation);
    }

    @Override
    public DesignationResponse getDesignationByCode(String code) {
        Designation designation = designationRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "code", code));
        return mapToResponse(designation);
    }

    @Override
    public PagedResponse<DesignationResponse> getAllDesignations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Designation> designationPage = designationRepository.findAll(pageable);

        List<DesignationResponse> content = designationPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<DesignationResponse>builder()
                .content(content)
                .page(designationPage.getNumber())
                .size(designationPage.getSize())
                .totalElements(designationPage.getTotalElements())
                .totalPages(designationPage.getTotalPages())
                .last(designationPage.isLast())
                .build();
    }

    @Override
    public List<DesignationResponse> getDesignationsByDepartment(String departmentId) {
        return designationRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDesignation(String id) {
        if (!designationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Designation", "id", id);
        }
        designationRepository.deleteById(id);
        log.info("Deleted designation ID: {}", id);
    }

    private DesignationResponse mapToResponse(Designation designation) {
        return DesignationResponse.builder()
                .id(designation.getId())
                .companyId(designation.getCompanyId())
                .departmentId(designation.getDepartmentId())
                .code(designation.getCode())
                .name(designation.getName())
                .description(designation.getDescription())
                .level(designation.getLevel())
                .status(designation.getStatus())
                .createdAt(designation.getCreatedAt())
                .updatedAt(designation.getUpdatedAt())
                .createdBy(designation.getCreatedBy())
                .updatedBy(designation.getUpdatedBy())
                .build();
    }
}
