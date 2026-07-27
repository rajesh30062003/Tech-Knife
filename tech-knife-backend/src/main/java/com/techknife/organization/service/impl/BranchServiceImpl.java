package com.techknife.organization.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.organization.dto.BranchRequest;
import com.techknife.organization.dto.BranchResponse;
import com.techknife.organization.entity.Branch;
import com.techknife.organization.entity.OrganizationStatus;
import com.techknife.organization.repository.BranchRepository;
import com.techknife.organization.repository.CompanyRepository;
import com.techknife.organization.service.BranchService;
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
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;

    @Override
    public BranchResponse createBranch(BranchRequest request) {
        if (branchRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Branch code already exists: " + request.getCode());
        }

        if (request.getCompanyId() != null && !request.getCompanyId().trim().isEmpty()) {
            if (!companyRepository.existsById(request.getCompanyId())) {
                throw new ResourceNotFoundException("Company", "id", request.getCompanyId());
            }
        }

        Branch branch = Branch.builder()
                .companyId(request.getCompanyId())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .isHeadquarters(Boolean.TRUE.equals(request.getIsHeadquarters()))
                .address(request.getAddress())
                .status(request.getStatus() != null ? request.getStatus() : OrganizationStatus.ACTIVE)
                .build();

        Branch saved = branchRepository.save(branch);
        log.info("Created branch ID: {} with code: {}", saved.getId(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    public BranchResponse updateBranch(String id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        if (!branch.getCode().equals(request.getCode()) && branchRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Branch code already exists: " + request.getCode());
        }

        if (request.getCompanyId() != null && !companyRepository.existsById(request.getCompanyId())) {
            throw new ResourceNotFoundException("Company", "id", request.getCompanyId());
        }

        branch.setCompanyId(request.getCompanyId());
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setDescription(request.getDescription());
        if (request.getIsHeadquarters() != null) {
            branch.setHeadquarters(request.getIsHeadquarters());
        }
        if (request.getAddress() != null) {
            branch.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            branch.setStatus(request.getStatus());
        }

        Branch updated = branchRepository.save(branch);
        log.info("Updated branch ID: {}", id);
        return mapToResponse(updated);
    }

    @Override
    public BranchResponse getBranchById(String id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));
        return mapToResponse(branch);
    }

    @Override
    public BranchResponse getBranchByCode(String code) {
        Branch branch = branchRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "code", code));
        return mapToResponse(branch);
    }

    @Override
    public PagedResponse<BranchResponse> getAllBranches(int page, int size, String companyId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Branch> branchPage;
        if (companyId != null && !companyId.trim().isEmpty()) {
            branchPage = branchRepository.findByCompanyId(companyId.trim(), pageable);
        } else {
            branchPage = branchRepository.findAll(pageable);
        }

        List<BranchResponse> content = branchPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<BranchResponse>builder()
                .content(content)
                .page(branchPage.getNumber())
                .size(branchPage.getSize())
                .totalElements(branchPage.getTotalElements())
                .totalPages(branchPage.getTotalPages())
                .last(branchPage.isLast())
                .build();
    }

    @Override
    public List<BranchResponse> getBranchesByCompany(String companyId) {
        return branchRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBranch(String id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch", "id", id);
        }
        branchRepository.deleteById(id);
        log.info("Deleted branch ID: {}", id);
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .companyId(branch.getCompanyId())
                .code(branch.getCode())
                .name(branch.getName())
                .description(branch.getDescription())
                .isHeadquarters(branch.isHeadquarters())
                .address(branch.getAddress())
                .status(branch.getStatus())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .createdBy(branch.getCreatedBy())
                .updatedBy(branch.getUpdatedBy())
                .build();
    }
}
