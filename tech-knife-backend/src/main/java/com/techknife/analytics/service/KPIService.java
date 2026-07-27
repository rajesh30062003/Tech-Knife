package com.techknife.analytics.service;

import com.techknife.analytics.dto.KPIDTO;
import com.techknife.analytics.dto.KPIGroupDTO;
import com.techknife.analytics.dto.KPIHistoryDTO;
import com.techknife.analytics.entity.KPICategory;

import java.util.List;

public interface KPIService {
    List<KPIDTO> getAllKPIs();
    KPIDTO getKPIById(String id);
    KPIDTO getKPIByCode(String code);
    List<KPIDTO> getKPIsByCategory(KPICategory category);
    KPIDTO createKPI(KPIDTO dto);
    KPIDTO updateKPI(String id, KPIDTO dto);
    void deleteKPI(String id);
    KPIDTO refreshKPIValue(String id);
    List<KPIHistoryDTO> getKPIHistory(String kpiId);
    KPIGroupDTO createKPIGroup(KPIGroupDTO dto);
    List<KPIGroupDTO> getAllKPIGroups();
}
