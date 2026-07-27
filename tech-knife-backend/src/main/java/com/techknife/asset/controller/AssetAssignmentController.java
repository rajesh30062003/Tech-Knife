package com.techknife.asset.controller;

import com.techknife.asset.dto.AssetAssignmentDTO;
import com.techknife.asset.dto.BulkAssetAssignRequest;
import com.techknife.asset.service.AssetAssignmentService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets/assignments")
@RequiredArgsConstructor
@Tag(name = "Asset - Assignments", description = "Asset Assignment Management API")
@SecurityRequirement(name = "bearerAuth")
public class AssetAssignmentController {

    private final AssetAssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ASSET_ASSIGN') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.ASSIGN, module = AuditModule.ASSET, entityType = "AssetAssignment", description = "Assigned Asset")
    @Operation(summary = "Assign Asset")
    public ResponseEntity<ApiResponse<AssetAssignmentDTO>> assignAsset(@Valid @RequestBody AssetAssignmentDTO dto) {
        AssetAssignmentDTO result = assignmentService.assignAsset(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Assigned asset successfully"));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ASSET_ASSIGN') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.ASSIGN, module = AuditModule.ASSET, entityType = "AssetAssignment", description = "Bulk Assigned Assets")
    @Operation(summary = "Bulk Assign Assets")
    public ResponseEntity<ApiResponse<List<AssetAssignmentDTO>>> bulkAssignAssets(@Valid @RequestBody BulkAssetAssignRequest request) {
        List<AssetAssignmentDTO> result = assignmentService.bulkAssignAssets(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Bulk assigned assets successfully"));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAuthority('ASSET_ASSIGN') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UNASSIGN, module = AuditModule.ASSET, entityType = "AssetAssignment", description = "Returned Asset")
    @Operation(summary = "Return Asset")
    public ResponseEntity<ApiResponse<AssetAssignmentDTO>> returnAsset(
            @PathVariable String id,
            @RequestParam(required = false) String returnCondition,
            @RequestParam(required = false) String notes) {
        AssetAssignmentDTO result = assignmentService.returnAsset(id, returnCondition, notes);
        return ResponseEntity.ok(ApiResponse.success(result, "Returned asset successfully"));
    }

    @PostMapping("/{id}/transfer")
    @PreAuthorize("hasAuthority('ASSET_TRANSFER') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ASSET, entityType = "AssetAssignment", description = "Transferred Asset")
    @Operation(summary = "Transfer Asset")
    public ResponseEntity<ApiResponse<AssetAssignmentDTO>> transferAsset(
            @PathVariable String id,
            @RequestParam String newEmployeeId,
            @RequestParam(required = false) String newEmployeeName,
            @RequestParam(required = false) String newDepartmentId,
            @RequestParam(required = false) String newDepartmentName,
            @RequestParam(required = false) String notes) {
        AssetAssignmentDTO result = assignmentService.transferAsset(id, newEmployeeId, newEmployeeName, newDepartmentId, newDepartmentName, notes);
        return ResponseEntity.ok(ApiResponse.success(result, "Transferred asset successfully"));
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Assignments by Asset")
    public ResponseEntity<ApiResponse<List<AssetAssignmentDTO>>> getAssignmentsByAsset(@PathVariable String assetId) {
        List<AssetAssignmentDTO> result = assignmentService.getAssignmentsByAsset(assetId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched assignments by asset"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Assignments by Employee")
    public ResponseEntity<ApiResponse<List<AssetAssignmentDTO>>> getAssignmentsByEmployee(@PathVariable String employeeId) {
        List<AssetAssignmentDTO> result = assignmentService.getAssignmentsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched assignments by employee"));
    }
}
