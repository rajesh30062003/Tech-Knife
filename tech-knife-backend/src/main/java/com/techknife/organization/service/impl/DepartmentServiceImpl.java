package com.techknife.organization.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.organization.dto.DepartmentRequest;
import com.techknife.organization.dto.DepartmentResponse;
import com.techknife.organization.entity.Department;
import com.techknife.organization.entity.OrganizationStatus;
import com.techknife.organization.repository.BranchRepository;
import com.techknife.organization.repository.CompanyRepository;
import com.techknife.organization.repository.DepartmentRepository;
import com.techknife.organization.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Department code already exists: " + request.getCode());
        }

        if (request.getCompanyId() != null && !request.getCompanyId().trim().isEmpty()) {
            if (!companyRepository.existsById(request.getCompanyId())) {
                throw new ResourceNotFoundException("Company", "id", request.getCompanyId());
            }
        }

        if (request.getBranchId() != null && !request.getBranchId().trim().isEmpty()) {
            if (!branchRepository.existsById(request.getBranchId())) {
                throw new ResourceNotFoundException("Branch", "id", request.getBranchId());
            }
        }

        Department department = Department.builder()
                .companyId(request.getCompanyId())
                .branchId(request.getBranchId())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .headId(request.getHeadId())
                .status(request.getStatus() != null ? request.getStatus() : OrganizationStatus.ACTIVE)
                .build();

        Department saved = departmentRepository.save(department);
        log.info("Created department ID: {} with code: {}", saved.getId(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    public DepartmentResponse updateDepartment(String id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!department.getCode().equals(request.getCode()) && departmentRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Department code already exists: " + request.getCode());
        }

        if (request.getCompanyId() != null && !companyRepository.existsById(request.getCompanyId())) {
            throw new ResourceNotFoundException("Company", "id", request.getCompanyId());
        }

        if (request.getBranchId() != null && !branchRepository.existsById(request.getBranchId())) {
            throw new ResourceNotFoundException("Branch", "id", request.getBranchId());
        }

        department.setCompanyId(request.getCompanyId());
        department.setBranchId(request.getBranchId());
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setHeadId(request.getHeadId());
        if (request.getStatus() != null) {
            department.setStatus(request.getStatus());
        }

        Department updated = departmentRepository.save(department);
        log.info("Updated department ID: {}", id);
        return mapToResponse(updated);
    }

    @Override
    public DepartmentResponse getDepartmentById(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return mapToResponse(department);
    }

    @Override
    public DepartmentResponse getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "code", code));
        return mapToResponse(department);
    }

    @Override
    public PagedResponse<DepartmentResponse> getAllDepartments(int page, int size, String companyId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Department> deptPage;
        if (companyId != null && !companyId.trim().isEmpty()) {
            deptPage = departmentRepository.findByCompanyId(companyId.trim(), pageable);
        } else {
            deptPage = departmentRepository.findAll(pageable);
        }

        List<DepartmentResponse> content = deptPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<DepartmentResponse>builder()
                .content(content)
                .page(deptPage.getNumber())
                .size(deptPage.getSize())
                .totalElements(deptPage.getTotalElements())
                .totalPages(deptPage.getTotalPages())
                .last(deptPage.isLast())
                .build();
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByCompany(String companyId) {
        return departmentRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByBranch(String branchId) {
        return departmentRepository.findByBranchId(branchId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDepartment(String id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", "id", id);
        }
        departmentRepository.deleteById(id);
        log.info("Deleted department ID: {}", id);
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .companyId(department.getCompanyId())
                .branchId(department.getBranchId())
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .headId(department.getHeadId())
                .status(department.getStatus())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .createdBy(department.getCreatedBy())
                .updatedBy(department.getUpdatedBy())
                .build();
    }
}
