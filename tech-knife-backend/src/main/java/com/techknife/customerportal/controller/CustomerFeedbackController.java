package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.CustomerFeedbackDTO;
import com.techknife.customerportal.service.CustomerFeedbackService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
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
@RequestMapping("/api/v1/customer/feedback")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Feedback", description = "Endpoints for Customer Ratings and Feedback")
@SecurityRequirement(name = "bearerAuth")
public class CustomerFeedbackController {

    private final CustomerFeedbackService customerFeedbackService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerFeedback", description = "Submitted Feedback")
    @Operation(summary = "Submit Customer Feedback and Rating")
    public ResponseEntity<ApiResponse<CustomerFeedbackDTO>> submitFeedback(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody CustomerFeedbackDTO dto) {
        CustomerFeedbackDTO result = customerFeedbackService.submitFeedback(userPrincipal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Submitted feedback successfully", result));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Feedback History")
    public ResponseEntity<ApiResponse<List<CustomerFeedbackDTO>>> getFeedbacks(@CurrentUser UserPrincipal userPrincipal) {
        List<CustomerFeedbackDTO> result = customerFeedbackService.getFeedbacks(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Fetched feedback history successfully", result));
    }
}
