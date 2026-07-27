package com.techknife.asset.service;

import com.techknife.asset.dto.AssetMaintenanceDTO;
import com.techknife.asset.dto.MaintenanceScheduleDTO;

import java.util.List;

public interface AssetMaintenanceService {
    AssetMaintenanceDTO scheduleMaintenance(AssetMaintenanceDTO dto);
    AssetMaintenanceDTO updateMaintenance(String id, AssetMaintenanceDTO dto);
    List<AssetMaintenanceDTO> getMaintenancesByAsset(String assetId);
    List<AssetMaintenanceDTO> getAllMaintenances();

    MaintenanceScheduleDTO createMaintenanceSchedule(MaintenanceScheduleDTO dto);
    List<MaintenanceScheduleDTO> getAllMaintenanceSchedules();
}
