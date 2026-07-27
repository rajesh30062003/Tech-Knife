package com.techknife.payroll.service;

import com.techknife.payroll.dto.SalaryComponentDTO;

import java.util.List;

public interface SalaryComponentService {
    List<SalaryComponentDTO> getAllComponents();
    SalaryComponentDTO getComponentById(String id);
    SalaryComponentDTO createComponent(SalaryComponentDTO dto);
    SalaryComponentDTO updateComponent(String id, SalaryComponentDTO dto);
    void deleteComponent(String id);
}
