package com.techknife.organization.service.impl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.organization.dto.CompanyRequest;
import com.techknife.organization.dto.CompanyResponse;
import com.techknife.organization.entity.Company;
import com.techknife.organization.entity.OrganizationStatus;
import com.techknife.organization.repository.CompanyRepository;
import com.techknife.organization.service.CompanyService;
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
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Company code already exists: " + request.getCode());
        }

        Company company = Company.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .email(request.getEmail())
                .phone(request.getPhone())
                .taxId(request.getTaxId())
                .registrationNumber(request.getRegistrationNumber())
                .address(request.getAddress())
                .status(request.getStatus() != null ? request.getStatus() : OrganizationStatus.ACTIVE)
                .build();

        Company saved = companyRepository.save(company);
        log.info("Created new company with ID: {} and code: {}", saved.getId(), saved.getCode());
        return mapToResponse(saved);
    }

    @Override
    public CompanyResponse updateCompany(String id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        if (!company.getCode().equals(request.getCode()) && companyRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Company code already exists: " + request.getCode());
        }

        company.setCode(request.getCode());
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setTaxId(request.getTaxId());
        company.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getAddress() != null) {
            company.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }

        Company updated = companyRepository.save(company);
        log.info("Updated company ID: {}", id);
        return mapToResponse(updated);
    }

    @Override
    public CompanyResponse getCompanyById(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return mapToResponse(company);
    }

    @Override
    public CompanyResponse getCompanyByCode(String code) {
        Company company = companyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "code", code));
        return mapToResponse(company);
    }

    @Override
    public PagedResponse<CompanyResponse> getAllCompanies(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Company> companyPage;
        if (search != null && !search.trim().isEmpty()) {
            companyPage = companyRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(search.trim(), search.trim(), pageable);
        } else {
            companyPage = companyRepository.findAll(pageable);
        }

        List<CompanyResponse> responses = companyPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<CompanyResponse>builder()
                .content(responses)
                .page(companyPage.getNumber())
                .size(companyPage.getSize())
                .totalElements(companyPage.getTotalElements())
                .totalPages(companyPage.getTotalPages())
                .last(companyPage.isLast())
                .build();
    }

    @Override
    public List<CompanyResponse> getAllActiveCompanies() {
        return companyRepository.findAll().stream()
                .filter(c -> c.getStatus() == OrganizationStatus.ACTIVE)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCompany(String id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", "id", id);
        }
        companyRepository.deleteById(id);
        log.info("Deleted company ID: {}", id);
    }

    private CompanyResponse mapToResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .code(company.getCode())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .email(company.getEmail())
                .phone(company.getPhone())
                .taxId(company.getTaxId())
                .registrationNumber(company.getRegistrationNumber())
                .address(company.getAddress())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .createdBy(company.getCreatedBy())
                .updatedBy(company.getUpdatedBy())
                .build();
    }
}
