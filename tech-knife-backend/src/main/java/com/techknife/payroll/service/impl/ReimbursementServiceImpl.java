package com.techknife.payroll.service.impl;

import com.techknife.payroll.dto.ReimbursementDTO;
import com.techknife.payroll.entity.Reimbursement;
import com.techknife.payroll.repository.ReimbursementRepository;
import com.techknife.payroll.service.ReimbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReimbursementServiceImpl implements ReimbursementService {

    private final ReimbursementRepository reimbursementRepository;

    @Override
    public List<ReimbursementDTO> getAllReimbursements() {
        return reimbursementRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReimbursementDTO> getReimbursementsByEmployeeId(String employeeId) {
        return reimbursementRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReimbursementDTO getReimbursementById(String id) {
        Reimbursement r = reimbursementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reimbursement record not found with id: " + id));
        return mapToDTO(r);
    }

    @Override
    public ReimbursementDTO submitReimbursement(ReimbursementDTO dto) {
        Reimbursement r = Reimbursement.builder()
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .expenseDate(dto.getExpenseDate())
                .receiptUrl(dto.getReceiptUrl())
                .approvalStatus(dto.getApprovalStatus() != null ? dto.getApprovalStatus() : "PENDING")
                .approvedBy(dto.getApprovedBy())
                .build();

        Reimbursement saved = reimbursementRepository.save(r);
        return mapToDTO(saved);
    }

    @Override
    public ReimbursementDTO updateApprovalStatus(String id, String approvalStatus, String approvedBy) {
        Reimbursement r = reimbursementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reimbursement record not found with id: " + id));

        r.setApprovalStatus(approvalStatus != null ? approvalStatus.toUpperCase() : "PENDING");
        if (approvedBy != null) {
            r.setApprovedBy(approvedBy);
        }
        Reimbursement saved = reimbursementRepository.save(r);
        return mapToDTO(saved);
    }

    private ReimbursementDTO mapToDTO(Reimbursement r) {
        return ReimbursementDTO.builder()
                .id(r.getId())
                .employeeId(r.getEmployeeId())
                .employeeName(r.getEmployeeName())
                .title(r.getTitle())
                .amount(r.getAmount())
                .expenseDate(r.getExpenseDate())
                .receiptUrl(r.getReceiptUrl())
                .approvalStatus(r.getApprovalStatus())
                .approvedBy(r.getApprovedBy())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }
}
