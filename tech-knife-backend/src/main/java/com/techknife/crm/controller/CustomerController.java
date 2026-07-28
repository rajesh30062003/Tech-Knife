package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.CustomerDTO;
import com.techknife.crm.service.CustomerService;
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
@RequestMapping("/api/v1/crm/customers")
@RequiredArgsConstructor
@Tag(name = "CRM - Customer Management", description = "Endpoints for managing CRM Customers")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all customers")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers(@RequestParam(required = false) String status) {
        List<CustomerDTO> result = customerService.getAllCustomers(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched customers successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(@PathVariable String id) {
        CustomerDTO result = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched customer successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER, entityType = "Customer", description = "Created CRM Customer")
    @Operation(summary = "Create a new customer")
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        CustomerDTO result = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created customer successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER, entityType = "Customer", description = "Updated CRM Customer")
    @Operation(summary = "Update an existing customer")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerDTO dto) {
        CustomerDTO result = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated customer successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CUSTOMER, entityType = "Customer", description = "Deleted CRM Customer")
    @Operation(summary = "Delete customer")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted customer successfully"));
    }

}
