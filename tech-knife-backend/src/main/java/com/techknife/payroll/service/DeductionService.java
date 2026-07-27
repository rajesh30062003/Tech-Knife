package com.techknife.payroll.service;

import com.techknife.payroll.dto.DeductionDTO;

import java.util.List;

public interface DeductionService {
    List<DeductionDTO> getAllDeductions();
    List<DeductionDTO> getDeductionsByEmployeeId(String employeeId);
    DeductionDTO getDeductionById(String id);
    DeductionDTO createDeduction(DeductionDTO dto);
    DeductionDTO updateDeduction(String id, DeductionDTO dto);
    void deleteDeduction(String id);
}
