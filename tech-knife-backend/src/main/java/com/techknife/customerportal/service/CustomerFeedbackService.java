package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.CustomerFeedbackDTO;

import java.util.List;

public interface CustomerFeedbackService {

    CustomerFeedbackDTO submitFeedback(String customerAccountId, CustomerFeedbackDTO dto);

    List<CustomerFeedbackDTO> getFeedbacks(String customerAccountId);
}
