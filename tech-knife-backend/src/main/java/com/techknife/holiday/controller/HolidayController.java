package com.techknife.holiday.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.holiday.dto.HolidayDTO;
import com.techknife.holiday.entity.HolidayType;
import com.techknife.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday Management", description = "Endpoints for managing National, State, Company, Branch, and Restricted Holidays")
@SecurityRequirement(name = "bearerAuth")
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_HOLIDAY", module = "HOLIDAY")
    @Operation(summary = "Create Holiday", description = "Defines a new holiday entry")
    public ResponseEntity<ApiResponse<HolidayDTO>> createHoliday(@Valid @RequestBody HolidayDTO dto) {
        HolidayDTO created = holidayService.createHoliday(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Holiday created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_HOLIDAY", module = "HOLIDAY")
    @Operation(summary = "Update Holiday", description = "Updates existing holiday details")
    public ResponseEntity<ApiResponse<HolidayDTO>> updateHoliday(@PathVariable String id, @Valid @RequestBody HolidayDTO dto) {
        HolidayDTO updated = holidayService.updateHoliday(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Holiday updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Holiday by ID")
    public ResponseEntity<ApiResponse<HolidayDTO>> getHolidayById(@PathVariable String id) {
        HolidayDTO result = holidayService.getHolidayById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Holiday details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Holidays", description = "Retrieves holidays by year, branch, or type")
    public ResponseEntity<ApiResponse<List<HolidayDTO>>> getHolidays(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String branchId,
            @RequestParam(required = false) HolidayType type) {
        int selectedYear = year != null ? year : Year.now().getValue();
        List<HolidayDTO> holidays;

        if (branchId != null) {
            holidays = holidayService.getHolidaysByYearAndBranch(selectedYear, branchId);
        } else if (type != null) {
            holidays = holidayService.getHolidaysByYearAndType(selectedYear, type);
        } else {
            holidays = holidayService.getHolidaysByYear(selectedYear);
        }

        return ResponseEntity.ok(ApiResponse.success(holidays, "Holidays retrieved successfully"));
    }

    @GetMapping("/range")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Holidays in Date Range")
    public ResponseEntity<ApiResponse<List<HolidayDTO>>> getHolidaysInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<HolidayDTO> holidays = holidayService.getHolidaysInRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(holidays, "Holidays in range retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_HOLIDAY", module = "HOLIDAY")
    @Operation(summary = "Delete Holiday")
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(@PathVariable String id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Holiday deleted successfully"));
    }
}
