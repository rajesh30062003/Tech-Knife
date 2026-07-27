package com.techknife.asset.service.impl;

import com.techknife.asset.dto.AssetAssignmentDTO;
import com.techknife.asset.dto.BulkAssetAssignRequest;
import com.techknife.asset.entity.Asset;
import com.techknife.asset.entity.AssetAssignment;
import com.techknife.asset.repository.AssetAssignmentRepository;
import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.service.AssetAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetAssignmentServiceImpl implements AssetAssignmentService {

    private final AssetAssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;

    @Override
    public AssetAssignmentDTO assignAsset(AssetAssignmentDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + dto.getAssetId()));

        AssetAssignment assignment = AssetAssignment.builder()
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getName())
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .departmentId(dto.getDepartmentId())
                .departmentName(dto.getDepartmentName())
                .assignmentDate(dto.getAssignmentDate() != null ? dto.getAssignmentDate() : LocalDate.now())
                .expectedReturnDate(dto.getExpectedReturnDate())
                .assignedBy(dto.getAssignedBy())
                .status("ACTIVE")
                .notes(dto.getNotes())
                .build();

        AssetAssignment saved = assignmentRepository.save(assignment);

        // Update asset status
        asset.setStatus("ASSIGNED");
        asset.setAssignedEmployeeId(dto.getEmployeeId());
        asset.setAssignedEmployeeName(dto.getEmployeeName());
        asset.setAssignedDepartmentId(dto.getDepartmentId());
        asset.setAssignedDepartmentName(dto.getDepartmentName());
        assetRepository.save(asset);

        return mapToDTO(saved);
    }

    @Override
    public AssetAssignmentDTO returnAsset(String assignmentId, String returnCondition, String notes) {
        AssetAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment record not found with id: " + assignmentId));

        assignment.setStatus("RETURNED");
        assignment.setActualReturnDate(LocalDate.now());
        assignment.setReturnCondition(returnCondition);
        if (notes != null) assignment.setNotes(notes);

        AssetAssignment saved = assignmentRepository.save(assignment);

        // Update asset status
        assetRepository.findById(assignment.getAssetId()).ifPresent(asset -> {
            asset.setStatus("AVAILABLE");
            asset.setAssignedEmployeeId(null);
            asset.setAssignedEmployeeName(null);
            asset.setAssignedDepartmentId(null);
            asset.setAssignedDepartmentName(null);
            assetRepository.save(asset);
        });

        return mapToDTO(saved);
    }

    @Override
    public AssetAssignmentDTO transferAsset(String assignmentId, String newEmployeeId, String newEmployeeName, String newDepartmentId, String newDepartmentName, String notes) {
        AssetAssignment currentAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment record not found with id: " + assignmentId));

        currentAssignment.setStatus("TRANSFERRED");
        currentAssignment.setActualReturnDate(LocalDate.now());
        assignmentRepository.save(currentAssignment);

        AssetAssignmentDTO newAssignmentDto = AssetAssignmentDTO.builder()
                .assetId(currentAssignment.getAssetId())
                .employeeId(newEmployeeId)
                .employeeName(newEmployeeName)
                .departmentId(newDepartmentId)
                .departmentName(newDepartmentName)
                .assignmentDate(LocalDate.now())
                .notes(notes)
                .build();

        return assignAsset(newAssignmentDto);
    }

    @Override
    public List<AssetAssignmentDTO> bulkAssignAssets(BulkAssetAssignRequest request) {
        List<AssetAssignmentDTO> results = new ArrayList<>();
        for (String assetId : request.getAssetIds()) {
            AssetAssignmentDTO dto = AssetAssignmentDTO.builder()
                    .assetId(assetId)
                    .employeeId(request.getEmployeeId())
                    .employeeName(request.getEmployeeName())
                    .departmentId(request.getDepartmentId())
                    .departmentName(request.getDepartmentName())
                    .assignmentDate(request.getAssignmentDate())
                    .expectedReturnDate(request.getExpectedReturnDate())
                    .notes(request.getNotes())
                    .build();
            results.add(assignAsset(dto));
        }
        return results;
    }

    @Override
    public List<AssetAssignmentDTO> getAssignmentsByAsset(String assetId) {
        return assignmentRepository.findByAssetId(assetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetAssignmentDTO> getAssignmentsByEmployee(String employeeId) {
        return assignmentRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AssetAssignmentDTO mapToDTO(AssetAssignment a) {
        return AssetAssignmentDTO.builder()
                .id(a.getId())
                .assetId(a.getAssetId())
                .assetCode(a.getAssetCode())
                .assetName(a.getAssetName())
                .employeeId(a.getEmployeeId())
                .employeeName(a.getEmployeeName())
                .departmentId(a.getDepartmentId())
                .departmentName(a.getDepartmentName())
                .assignmentDate(a.getAssignmentDate())
                .expectedReturnDate(a.getExpectedReturnDate())
                .actualReturnDate(a.getActualReturnDate())
                .assignedBy(a.getAssignedBy())
                .status(a.getStatus())
                .returnCondition(a.getReturnCondition())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
