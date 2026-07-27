package com.techknife.payroll.service;

import com.techknife.payroll.dto.PayrollRunDTO;

import java.util.List;

public interface PayrollRunService {
    List<PayrollRunDTO> getAllRuns();
    List<PayrollRunDTO> getRunsByCycleId(String cycleId);
    PayrollRunDTO getRunById(String id);
    PayrollRunDTO processPayrollRun(PayrollRunDTO dto);
    PayrollRunDTO updateRunStatus(String id, String status);
}
