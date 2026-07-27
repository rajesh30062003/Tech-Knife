package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.BonusDTO;
import com.techknife.payroll.service.BonusService;
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
@RequestMapping("/api/v1/payroll/bonuses")
@RequiredArgsConstructor
@Tag(name = "Payroll - Bonuses", description = "Manage performance and annual employee bonuses")
@SecurityRequirement(name = "bearerAuth")
public class BonusController {

    private final BonusService bonusService;

    @GetMapping
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Bonuses")
    public ResponseEntity<ApiResponse<List<BonusDTO>>> getAllBonuses() {
        List<BonusDTO> result = bonusService.getAllBonuses();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched bonus records successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Bonuses by Employee ID")
    public ResponseEntity<ApiResponse<List<BonusDTO>>> getBonusesByEmployeeId(@PathVariable String employeeId) {
        List<BonusDTO> result = bonusService.getBonusesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee bonuses successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Bonus by ID")
    public ResponseEntity<ApiResponse<BonusDTO>> getBonusById(@PathVariable String id) {
        BonusDTO result = bonusService.getBonusById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched bonus details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "Bonus", description = "Created Bonus Record")
    @Operation(summary = "Create Bonus Record")
    public ResponseEntity<ApiResponse<BonusDTO>> createBonus(@Valid @RequestBody BonusDTO dto) {
        BonusDTO result = bonusService.createBonus(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created bonus record successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('BONUS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "Bonus", description = "Updated Bonus Status")
    @Operation(summary = "Update Bonus Status")
    public ResponseEntity<ApiResponse<BonusDTO>> updateBonusStatus(@PathVariable String id, @RequestParam String status) {
        BonusDTO result = bonusService.updateBonusStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated bonus status successfully"));
    }
}
