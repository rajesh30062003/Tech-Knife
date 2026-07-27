package com.techknife.finance.service;

import com.techknife.finance.dto.CostCenterDTO;

import java.util.List;

public interface CostCenterService {

    List<CostCenterDTO> getAllCostCenters();

    List<CostCenterDTO> getCostCentersByType(String type);

    CostCenterDTO getCostCenterById(String id);

    CostCenterDTO createCostCenter(CostCenterDTO dto);

    CostCenterDTO updateCostCenter(String id, CostCenterDTO dto);

    void deleteCostCenter(String id);
}
