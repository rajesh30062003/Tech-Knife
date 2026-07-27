package com.techknife.asset.service.impl;

import com.techknife.asset.dto.AssetMaintenanceDTO;
import com.techknife.asset.dto.MaintenanceScheduleDTO;
import com.techknife.asset.entity.Asset;
import com.techknife.asset.entity.AssetMaintenance;
import com.techknife.asset.entity.MaintenanceSchedule;
import com.techknife.asset.repository.AssetMaintenanceRepository;
import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.repository.MaintenanceScheduleRepository;
import com.techknife.asset.service.AssetMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetMaintenanceServiceImpl implements AssetMaintenanceService {

    private final AssetMaintenanceRepository maintenanceRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final AssetRepository assetRepository;

    @Override
    public AssetMaintenanceDTO scheduleMaintenance(AssetMaintenanceDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + dto.getAssetId()));

        AssetMaintenance maintenance = AssetMaintenance.builder()
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getName())
                .maintenanceType(dto.getMaintenanceType() != null ? dto.getMaintenanceType() : "PREVENTIVE")
                .serviceProvider(dto.getServiceProvider())
                .contactPerson(dto.getContactPerson())
                .maintenanceDate(dto.getMaintenanceDate())
                .completionDate(dto.getCompletionDate())
                .cost(dto.getCost())
                .status(dto.getStatus() != null ? dto.getStatus() : "SCHEDULED")
                .description(dto.getDescription())
                .resolution(dto.getResolution())
                .amcContractNumber(dto.getAmcContractNumber())
                .build();

        AssetMaintenance saved = maintenanceRepository.save(maintenance);

        // Update asset status
        asset.setStatus("UNDER_MAINTENANCE");
        assetRepository.save(asset);

        return mapToDTO(saved);
    }

    @Override
    public AssetMaintenanceDTO updateMaintenance(String id, AssetMaintenanceDTO dto) {
        AssetMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found with id: " + id));

        if (dto.getServiceProvider() != null) maintenance.setServiceProvider(dto.getServiceProvider());
        if (dto.getContactPerson() != null) maintenance.setContactPerson(dto.getContactPerson());
        if (dto.getMaintenanceDate() != null) maintenance.setMaintenanceDate(dto.getMaintenanceDate());
        if (dto.getCompletionDate() != null) maintenance.setCompletionDate(dto.getCompletionDate());
        if (dto.getCost() != null) maintenance.setCost(dto.getCost());
        if (dto.getStatus() != null) maintenance.setStatus(dto.getStatus());
        if (dto.getDescription() != null) maintenance.setDescription(dto.getDescription());
        if (dto.getResolution() != null) maintenance.setResolution(dto.getResolution());
        if (dto.getAmcContractNumber() != null) maintenance.setAmcContractNumber(dto.getAmcContractNumber());

        AssetMaintenance saved = maintenanceRepository.save(maintenance);

        // If completed or cancelled, update asset status back to AVAILABLE
        if ("COMPLETED".equalsIgnoreCase(saved.getStatus()) || "CANCELLED".equalsIgnoreCase(saved.getStatus())) {
            assetRepository.findById(saved.getAssetId()).ifPresent(asset -> {
                asset.setStatus("AVAILABLE");
                assetRepository.save(asset);
            });
        }

        return mapToDTO(saved);
    }

    @Override
    public List<AssetMaintenanceDTO> getMaintenancesByAsset(String assetId) {
        return maintenanceRepository.findByAssetId(assetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetMaintenanceDTO> getAllMaintenances() {
        return maintenanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceScheduleDTO createMaintenanceSchedule(MaintenanceScheduleDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + dto.getAssetId()));

        MaintenanceSchedule schedule = MaintenanceSchedule.builder()
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .title(dto.getTitle())
                .frequency(dto.getFrequency() != null ? dto.getFrequency() : "ONE_TIME")
                .scheduledDate(dto.getScheduledDate())
                .assignedTechnician(dto.getAssignedTechnician())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .notes(dto.getNotes())
                .build();

        MaintenanceSchedule saved = scheduleRepository.save(schedule);
        return mapToScheduleDTO(saved);
    }

    @Override
    public List<MaintenanceScheduleDTO> getAllMaintenanceSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::mapToScheduleDTO)
                .collect(Collectors.toList());
    }

    private AssetMaintenanceDTO mapToDTO(AssetMaintenance m) {
        return AssetMaintenanceDTO.builder()
                .id(m.getId())
                .assetId(m.getAssetId())
                .assetCode(m.getAssetCode())
                .assetName(m.getAssetName())
                .maintenanceType(m.getMaintenanceType())
                .serviceProvider(m.getServiceProvider())
                .contactPerson(m.getContactPerson())
                .maintenanceDate(m.getMaintenanceDate())
                .completionDate(m.getCompletionDate())
                .cost(m.getCost())
                .status(m.getStatus())
                .description(m.getDescription())
                .resolution(m.getResolution())
                .amcContractNumber(m.getAmcContractNumber())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private MaintenanceScheduleDTO mapToScheduleDTO(MaintenanceSchedule s) {
        return MaintenanceScheduleDTO.builder()
                .id(s.getId())
                .assetId(s.getAssetId())
                .assetCode(s.getAssetCode())
                .title(s.getTitle())
                .frequency(s.getFrequency())
                .scheduledDate(s.getScheduledDate())
                .assignedTechnician(s.getAssignedTechnician())
                .status(s.getStatus())
                .notes(s.getNotes())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
