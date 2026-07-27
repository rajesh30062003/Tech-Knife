package com.techknife.payroll.service;

import com.techknife.payroll.dto.ReimbursementDTO;

import java.util.List;

public interface ReimbursementService {
    List<ReimbursementDTO> getAllReimbursements();
    List<ReimbursementDTO> getReimbursementsByEmployeeId(String employeeId);
    ReimbursementDTO getReimbursementById(String id);
    ReimbursementDTO submitReimbursement(ReimbursementDTO dto);
    ReimbursementDTO updateApprovalStatus(String id, String approvalStatus, String approvedBy);
}
