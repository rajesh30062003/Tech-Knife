package com.techknife.finance.service.impl;

import com.techknife.finance.dto.FinancialYearDTO;
import com.techknife.finance.entity.FinancialYear;
import com.techknife.finance.repository.FinancialYearRepository;
import com.techknife.finance.service.FinancialYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialYearServiceImpl implements FinancialYearService {

    private final FinancialYearRepository financialYearRepository;

    @Override
    public List<FinancialYearDTO> getAllFinancialYears() {
        return financialYearRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FinancialYearDTO getFinancialYearById(String id) {
        FinancialYear fy = financialYearRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Financial year not found with id: " + id));
        return mapToDTO(fy);
    }

    @Override
    public FinancialYearDTO createFinancialYear(FinancialYearDTO dto) {
        if (financialYearRepository.existsByYearCode(dto.getYearCode())) {
            throw new IllegalArgumentException("Financial year code already exists: " + dto.getYearCode());
        }

        FinancialYear fy = FinancialYear.builder()
                .yearCode(dto.getYearCode())
                .yearName(dto.getYearName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "PLANNING")
                .isLocked(dto.getIsLocked() != null ? dto.getIsLocked() : false)
                .notes(dto.getNotes())
                .build();

        FinancialYear saved = financialYearRepository.save(fy);
        return mapToDTO(saved);
    }

    @Override
    public FinancialYearDTO updateFinancialYearStatus(String id, String status, Boolean isLocked) {
        FinancialYear fy = financialYearRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Financial year not found with id: " + id));

        String upperStatus = status.toUpperCase();
        fy.setStatus(upperStatus);

        if (isLocked != null) {
            fy.setIsLocked(isLocked);
        } else if ("LOCKED".equals(upperStatus) || "CLOSED".equals(upperStatus) || "ARCHIVED".equals(upperStatus)) {
            fy.setIsLocked(true);
        } else {
            fy.setIsLocked(false);
        }

        FinancialYear saved = financialYearRepository.save(fy);
        return mapToDTO(saved);
    }

    private FinancialYearDTO mapToDTO(FinancialYear fy) {
        return FinancialYearDTO.builder()
                .id(fy.getId())
                .yearCode(fy.getYearCode())
                .yearName(fy.getYearName())
                .startDate(fy.getStartDate())
                .endDate(fy.getEndDate())
                .status(fy.getStatus())
                .isLocked(fy.getIsLocked())
                .notes(fy.getNotes())
                .createdAt(fy.getCreatedAt())
                .updatedAt(fy.getUpdatedAt())
                .createdBy(fy.getCreatedBy())
                .updatedBy(fy.getUpdatedBy())
                .build();
    }
}
