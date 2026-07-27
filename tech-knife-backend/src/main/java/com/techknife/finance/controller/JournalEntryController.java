package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.JournalEntryDTO;
import com.techknife.finance.service.JournalEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/journal-entries")
@RequiredArgsConstructor
@Tag(name = "Finance - Journal Entries", description = "Manage Journal Entries and Postings")
@SecurityRequirement(name = "bearerAuth")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('JOURNAL_POST') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Journal Entries")
    public ResponseEntity<ApiResponse<List<JournalEntryDTO>>> getAllJournalEntries(@RequestParam(required = false) String financialYearId) {
        List<JournalEntryDTO> result = financialYearId != null && !financialYearId.isBlank()
                ? journalEntryService.getJournalEntriesByFinancialYear(financialYearId)
                : journalEntryService.getAllJournalEntries();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched journal entries successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('JOURNAL_POST') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Journal Entry by ID")
    public ResponseEntity<ApiResponse<JournalEntryDTO>> getJournalEntryById(@PathVariable String id) {
        JournalEntryDTO result = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched journal entry successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('JOURNAL_POST') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "JournalEntry", description = "Created Journal Entry")
    @Operation(summary = "Create Journal Entry (Draft)")
    public ResponseEntity<ApiResponse<JournalEntryDTO>> createJournalEntry(@Valid @RequestBody JournalEntryDTO dto) {
        JournalEntryDTO result = journalEntryService.createJournalEntry(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created journal entry successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.APPROVE, module = AuditModule.FINANCE, entityType = "JournalEntry", description = "Approved Journal Entry")
    @Operation(summary = "Approve Journal Entry")
    public ResponseEntity<ApiResponse<JournalEntryDTO>> approveJournalEntry(@PathVariable String id, Principal principal) {
        String username = principal != null ? principal.getName() : "ADMIN";
        JournalEntryDTO result = journalEntryService.approveJournalEntry(id, username);
        return ResponseEntity.ok(ApiResponse.success(result, "Approved journal entry successfully"));
    }

    @PostMapping("/{id}/post")
    @PreAuthorize("hasAuthority('JOURNAL_POST') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.POST, module = AuditModule.FINANCE, entityType = "JournalEntry", description = "Posted Journal Entry to Ledger")
    @Operation(summary = "Post Journal Entry to General Ledger")
    public ResponseEntity<ApiResponse<JournalEntryDTO>> postJournalEntry(@PathVariable String id, Principal principal) {
        String username = principal != null ? principal.getName() : "ADMIN";
        JournalEntryDTO result = journalEntryService.postJournalEntry(id, username);
        return ResponseEntity.ok(ApiResponse.success(result, "Posted journal entry successfully"));
    }

    @PostMapping("/{id}/reverse")
    @PreAuthorize("hasAuthority('JOURNAL_POST') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "JournalEntry", description = "Reversed Journal Entry")
    @Operation(summary = "Reverse Journal Entry")
    public ResponseEntity<ApiResponse<JournalEntryDTO>> reverseJournalEntry(@PathVariable String id, Principal principal) {
        String username = principal != null ? principal.getName() : "ADMIN";
        JournalEntryDTO result = journalEntryService.reverseJournalEntry(id, username);
        return ResponseEntity.ok(ApiResponse.success(result, "Reversed journal entry successfully"));
    }
}
