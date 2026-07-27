package com.techknife.payroll.service;

import com.techknife.payroll.dto.SalaryStructureDTO;

import java.util.List;

public interface SalaryStructureService {
    List<SalaryStructureDTO> getAllStructures();
    SalaryStructureDTO getStructureById(String id);
    SalaryStructureDTO createStructure(SalaryStructureDTO dto);
    SalaryStructureDTO updateStructure(String id, SalaryStructureDTO dto);
    void deleteStructure(String id);
}
