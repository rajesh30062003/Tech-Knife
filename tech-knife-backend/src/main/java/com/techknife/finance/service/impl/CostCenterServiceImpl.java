package com.techknife.finance.service.impl;

import com.techknife.finance.dto.CostCenterDTO;
import com.techknife.finance.entity.CostCenter;
import com.techknife.finance.repository.CostCenterRepository;
import com.techknife.finance.service.CostCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostCenterServiceImpl implements CostCenterService {

    private final CostCenterRepository costCenterRepository;

    @Override
    public List<CostCenterDTO> getAllCostCenters() {
        return costCenterRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CostCenterDTO> getCostCentersByType(String type) {
        return costCenterRepository.findByType(type.toUpperCase()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CostCenterDTO getCostCenterById(String id) {
        CostCenter cc = costCenterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cost center not found with id: " + id));
        return mapToDTO(cc);
    }

    @Override
    public CostCenterDTO createCostCenter(CostCenterDTO dto) {
        if (costCenterRepository.existsByCenterCode(dto.getCenterCode())) {
            throw new IllegalArgumentException("Cost center code already exists: " + dto.getCenterCode());
        }

        CostCenter cc = CostCenter.builder()
                .centerCode(dto.getCenterCode())
                .centerName(dto.getCenterName())
                .type(dto.getType().toUpperCase())
                .departmentId(dto.getDepartmentId())
                .projectId(dto.getProjectId())
                .branchName(dto.getBranchName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        CostCenter saved = costCenterRepository.save(cc);
        return mapToDTO(saved);
    }

    @Override
    public CostCenterDTO updateCostCenter(String id, CostCenterDTO dto) {
        CostCenter cc = costCenterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cost center not found with id: " + id));

        if (dto.getCenterName() != null) cc.setCenterName(dto.getCenterName());
        if (dto.getType() != null) cc.setType(dto.getType().toUpperCase());
        if (dto.getDepartmentId() != null) cc.setDepartmentId(dto.getDepartmentId());
        if (dto.getProjectId() != null) cc.setProjectId(dto.getProjectId());
        if (dto.getBranchName() != null) cc.setBranchName(dto.getBranchName());
        if (dto.getDescription() != null) cc.setDescription(dto.getDescription());
        if (dto.getStatus() != null) cc.setStatus(dto.getStatus());

        CostCenter saved = costCenterRepository.save(cc);
        return mapToDTO(saved);
    }

    @Override
    public void deleteCostCenter(String id) {
        if (!costCenterRepository.existsById(id)) {
            throw new IllegalArgumentException("Cost center not found with id: " + id);
        }
        costCenterRepository.deleteById(id);
    }

    private CostCenterDTO mapToDTO(CostCenter cc) {
        return CostCenterDTO.builder()
                .id(cc.getId())
                .centerCode(cc.getCenterCode())
                .centerName(cc.getCenterName())
                .type(cc.getType())
                .departmentId(cc.getDepartmentId())
                .projectId(cc.getProjectId())
                .branchName(cc.getBranchName())
                .description(cc.getDescription())
                .status(cc.getStatus())
                .createdAt(cc.getCreatedAt())
                .updatedAt(cc.getUpdatedAt())
                .createdBy(cc.getCreatedBy())
                .updatedBy(cc.getUpdatedBy())
                .build();
    }
}
