package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.CustomerSearchDTO;

public interface CustomerSearchService {

    CustomerSearchDTO search(String customerAccountId, String query);
}
