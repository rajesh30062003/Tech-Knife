package com.techknife.analytics.service;

import com.techknife.analytics.dto.SystemHealthDTO;

public interface SystemHealthService {
    SystemHealthDTO getCurrentSystemHealth();
    SystemHealthDTO captureSystemHealthSnapshot();
}
