package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.CustomerFeedbackDTO;
import com.techknife.customerportal.entity.CustomerAccount;
import com.techknife.customerportal.entity.CustomerFeedback;
import com.techknife.customerportal.repository.CustomerAccountRepository;
import com.techknife.customerportal.repository.CustomerFeedbackRepository;
import com.techknife.customerportal.service.CustomerFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerFeedbackServiceImpl implements CustomerFeedbackService {

    private final CustomerFeedbackRepository customerFeedbackRepository;
    private final CustomerAccountRepository customerAccountRepository;

    @Override
    public CustomerFeedbackDTO submitFeedback(String customerAccountId, CustomerFeedbackDTO dto) {
        CustomerAccount account = customerAccountRepository.findById(customerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found"));

        double projRating = dto.getProjectRating() != null ? dto.getProjectRating() : 5.0;
        double empRating = dto.getEmployeeRating() != null ? dto.getEmployeeRating() : 5.0;
        double satisfactionScore = ((projRating + empRating) / 10.0) * 10.0;

        CustomerFeedback feedback = CustomerFeedback.builder()
                .customerAccountId(customerAccountId)
                .customerName(account.getContactPersonName())
                .projectId(dto.getProjectId())
                .projectName(dto.getProjectName())
                .employeeId(dto.getEmployeeId())
                .employeeName(dto.getEmployeeName())
                .projectRating(dto.getProjectRating())
                .employeeRating(dto.getEmployeeRating())
                .comments(dto.getComments())
                .suggestions(dto.getSuggestions())
                .satisfactionScore(satisfactionScore)
                .build();

        CustomerFeedback saved = customerFeedbackRepository.save(feedback);
        return mapToDTO(saved);
    }

    @Override
    public List<CustomerFeedbackDTO> getFeedbacks(String customerAccountId) {
        return customerFeedbackRepository.findByCustomerAccountId(customerAccountId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CustomerFeedbackDTO mapToDTO(CustomerFeedback f) {
        return CustomerFeedbackDTO.builder()
                .id(f.getId())
                .customerAccountId(f.getCustomerAccountId())
                .customerName(f.getCustomerName())
                .projectId(f.getProjectId())
                .projectName(f.getProjectName())
                .employeeId(f.getEmployeeId())
                .employeeName(f.getEmployeeName())
                .projectRating(f.getProjectRating())
                .employeeRating(f.getEmployeeRating())
                .comments(f.getComments())
                .suggestions(f.getSuggestions())
                .satisfactionScore(f.getSatisfactionScore())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
