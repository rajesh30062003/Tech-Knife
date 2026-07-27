package com.techknife.asset.service;

import com.techknife.asset.dto.AssetAssignmentDTO;
import com.techknife.asset.dto.BulkAssetAssignRequest;

import java.util.List;

public interface AssetAssignmentService {
    AssetAssignmentDTO assignAsset(AssetAssignmentDTO dto);
    AssetAssignmentDTO returnAsset(String assignmentId, String returnCondition, String notes);
    AssetAssignmentDTO transferAsset(String assignmentId, String newEmployeeId, String newEmployeeName, String newDepartmentId, String newDepartmentName, String notes);
    List<AssetAssignmentDTO> bulkAssignAssets(BulkAssetAssignRequest request);
    List<AssetAssignmentDTO> getAssignmentsByAsset(String assetId);
    List<AssetAssignmentDTO> getAssignmentsByEmployee(String employeeId);
}
