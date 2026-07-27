package com.techknife.payroll.service;

import com.techknife.payroll.dto.BonusDTO;

import java.util.List;

public interface BonusService {
    List<BonusDTO> getAllBonuses();
    List<BonusDTO> getBonusesByEmployeeId(String employeeId);
    BonusDTO getBonusById(String id);
    BonusDTO createBonus(BonusDTO dto);
    BonusDTO updateBonusStatus(String id, String status);
}
