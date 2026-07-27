package com.techknife.asset.service.impl;

import com.techknife.asset.dto.AssetDTO;
import com.techknife.asset.entity.Asset;
import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetDTO getAssetById(String id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + id));
        return mapToDTO(asset);
    }

    @Override
    public AssetDTO getAssetByCode(String assetCode) {
        Asset asset = assetRepository.findByAssetCode(assetCode)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with code: " + assetCode));
        return mapToDTO(asset);
    }

    @Override
    public AssetDTO createAsset(AssetDTO dto) {
        if (assetRepository.existsByAssetCode(dto.getAssetCode())) {
            throw new IllegalArgumentException("Asset already exists with code: " + dto.getAssetCode());
        }
        if (dto.getSerialNumber() != null && !dto.getSerialNumber().isBlank() && assetRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new IllegalArgumentException("Asset already exists with serial number: " + dto.getSerialNumber());
        }

        if (dto.getWarrantyStartDate() != null && dto.getWarrantyEndDate() != null && dto.getWarrantyEndDate().isBefore(dto.getWarrantyStartDate())) {
            throw new IllegalArgumentException("Warranty end date cannot be before warranty start date");
        }

        Asset asset = Asset.builder()
                .assetCode(dto.getAssetCode())
                .name(dto.getName())
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .serialNumber(dto.getSerialNumber())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .configuration(dto.getConfiguration())
                .purchaseDate(dto.getPurchaseDate())
                .purchaseCost(dto.getPurchaseCost())
                .warrantyStartDate(dto.getWarrantyStartDate())
                .warrantyEndDate(dto.getWarrantyEndDate())
                .assignedEmployeeId(dto.getAssignedEmployeeId())
                .assignedEmployeeName(dto.getAssignedEmployeeName())
                .assignedDepartmentId(dto.getAssignedDepartmentId())
                .assignedDepartmentName(dto.getAssignedDepartmentName())
                .assignedBranchId(dto.getAssignedBranchId())
                .status(dto.getStatus() != null ? dto.getStatus() : (dto.getAssignedEmployeeId() != null ? "ASSIGNED" : "AVAILABLE"))
                .currentLocation(dto.getCurrentLocation())
                .qrCode(dto.getQrCode())
                .barcode(dto.getBarcode())
                .remarks(dto.getRemarks())
                .build();

        Asset saved = assetRepository.save(asset);
        return mapToDTO(saved);
    }

    @Override
    public AssetDTO updateAsset(String id, AssetDTO dto) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + id));

        if (dto.getSerialNumber() != null && !dto.getSerialNumber().equals(asset.getSerialNumber())) {
            if (assetRepository.existsBySerialNumber(dto.getSerialNumber())) {
                throw new IllegalArgumentException("Asset already exists with serial number: " + dto.getSerialNumber());
            }
            asset.setSerialNumber(dto.getSerialNumber());
        }

        if (dto.getWarrantyStartDate() != null && dto.getWarrantyEndDate() != null && dto.getWarrantyEndDate().isBefore(dto.getWarrantyStartDate())) {
            throw new IllegalArgumentException("Warranty end date cannot be before warranty start date");
        }

        if (dto.getName() != null) asset.setName(dto.getName());
        if (dto.getCategoryId() != null) asset.setCategoryId(dto.getCategoryId());
        if (dto.getCategoryName() != null) asset.setCategoryName(dto.getCategoryName());
        if (dto.getBrand() != null) asset.setBrand(dto.getBrand());
        if (dto.getModel() != null) asset.setModel(dto.getModel());
        if (dto.getConfiguration() != null) asset.setConfiguration(dto.getConfiguration());
        if (dto.getPurchaseDate() != null) asset.setPurchaseDate(dto.getPurchaseDate());
        if (dto.getPurchaseCost() != null) asset.setPurchaseCost(dto.getPurchaseCost());
        if (dto.getWarrantyStartDate() != null) asset.setWarrantyStartDate(dto.getWarrantyStartDate());
        if (dto.getWarrantyEndDate() != null) asset.setWarrantyEndDate(dto.getWarrantyEndDate());
        if (dto.getAssignedEmployeeId() != null) asset.setAssignedEmployeeId(dto.getAssignedEmployeeId());
        if (dto.getAssignedEmployeeName() != null) asset.setAssignedEmployeeName(dto.getAssignedEmployeeName());
        if (dto.getAssignedDepartmentId() != null) asset.setAssignedDepartmentId(dto.getAssignedDepartmentId());
        if (dto.getAssignedDepartmentName() != null) asset.setAssignedDepartmentName(dto.getAssignedDepartmentName());
        if (dto.getAssignedBranchId() != null) asset.setAssignedBranchId(dto.getAssignedBranchId());
        if (dto.getStatus() != null) asset.setStatus(dto.getStatus());
        if (dto.getCurrentLocation() != null) asset.setCurrentLocation(dto.getCurrentLocation());
        if (dto.getQrCode() != null) asset.setQrCode(dto.getQrCode());
        if (dto.getBarcode() != null) asset.setBarcode(dto.getBarcode());
        if (dto.getRemarks() != null) asset.setRemarks(dto.getRemarks());

        Asset saved = assetRepository.save(asset);
        return mapToDTO(saved);
    }

    @Override
    public void deleteAsset(String id) {
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("Asset not found with id: " + id);
        }
        assetRepository.deleteById(id);
    }

    @Override
    public List<AssetDTO> getAssetsByStatus(String status) {
        return assetRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDTO> getAssetsByEmployee(String employeeId) {
        return assetRepository.findByAssignedEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AssetDTO mapToDTO(Asset a) {
        return AssetDTO.builder()
                .id(a.getId())
                .assetCode(a.getAssetCode())
                .name(a.getName())
                .categoryId(a.getCategoryId())
                .categoryName(a.getCategoryName())
                .serialNumber(a.getSerialNumber())
                .brand(a.getBrand())
                .model(a.getModel())
                .configuration(a.getConfiguration())
                .purchaseDate(a.getPurchaseDate())
                .purchaseCost(a.getPurchaseCost())
                .warrantyStartDate(a.getWarrantyStartDate())
                .warrantyEndDate(a.getWarrantyEndDate())
                .assignedEmployeeId(a.getAssignedEmployeeId())
                .assignedEmployeeName(a.getAssignedEmployeeName())
                .assignedDepartmentId(a.getAssignedDepartmentId())
                .assignedDepartmentName(a.getAssignedDepartmentName())
                .assignedBranchId(a.getAssignedBranchId())
                .status(a.getStatus())
                .currentLocation(a.getCurrentLocation())
                .qrCode(a.getQrCode())
                .barcode(a.getBarcode())
                .remarks(a.getRemarks())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .createdBy(a.getCreatedBy())
                .updatedBy(a.getUpdatedBy())
                .build();
    }
}
