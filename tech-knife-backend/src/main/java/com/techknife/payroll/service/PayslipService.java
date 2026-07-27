package com.techknife.payroll.service;

import com.techknife.payroll.dto.PayslipDTO;

import java.util.List;

public interface PayslipService {
    List<PayslipDTO> getAllPayslips();
    List<PayslipDTO> getPayslipsByEmployeeId(String employeeId);
    List<PayslipDTO> getPayslipsByRunId(String runId);
    PayslipDTO getPayslipById(String id);
    PayslipDTO generatePayslip(PayslipDTO dto);
}
