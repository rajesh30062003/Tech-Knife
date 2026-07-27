package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.CustomerAnalyticsDTO;
import com.techknife.customerportal.dto.CustomerDashboardDTO;

public interface CustomerDashboardService {

    CustomerDashboardDTO getDashboard(String customerAccountId);

    CustomerAnalyticsDTO getAnalytics(String customerAccountId);
}
