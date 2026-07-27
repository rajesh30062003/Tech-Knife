package com.techknife.payroll.service;

import com.techknife.payroll.dto.PayrollCycleDTO;

import java.util.List;

public interface PayrollCycleService {
    List<PayrollCycleDTO> getAllCycles();
    PayrollCycleDTO getCycleById(String id);
    PayrollCycleDTO createCycle(PayrollCycleDTO dto);
    PayrollCycleDTO updateCycle(String id, PayrollCycleDTO dto);
    void deleteCycle(String id);
}
