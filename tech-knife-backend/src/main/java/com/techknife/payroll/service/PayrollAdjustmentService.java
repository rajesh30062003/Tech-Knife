package com.techknife.payroll.service;

import com.techknife.payroll.dto.PayrollAdjustmentDTO;

import java.util.List;

public interface PayrollAdjustmentService {
    List<PayrollAdjustmentDTO> getAllAdjustments();
    List<PayrollAdjustmentDTO> getAdjustmentsByEmployeeId(String employeeId);
    List<PayrollAdjustmentDTO> getAdjustmentsByCycleId(String cycleId);
    PayrollAdjustmentDTO getAdjustmentById(String id);
    PayrollAdjustmentDTO createAdjustment(PayrollAdjustmentDTO dto);
    PayrollAdjustmentDTO updateAdjustmentStatus(String id, String status);
}
