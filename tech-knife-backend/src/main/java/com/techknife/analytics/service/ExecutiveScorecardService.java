package com.techknife.analytics.service;

import com.techknife.analytics.dto.ExecutiveScorecardDTO;
import com.techknife.analytics.entity.ExecutiveRole;

import java.util.List;

public interface ExecutiveScorecardService {
    List<ExecutiveScorecardDTO> getAllScorecards();
    ExecutiveScorecardDTO getScorecardByRoleAndPeriod(ExecutiveRole role, String period);
    ExecutiveScorecardDTO generateExecutiveScorecard(ExecutiveRole role, String period);
}
