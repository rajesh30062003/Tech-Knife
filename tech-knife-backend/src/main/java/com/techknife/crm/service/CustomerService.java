package com.techknife.crm.service;

import com.techknife.crm.dto.CustomerDTO;
import com.techknife.crm.entity.Customer;
import com.techknife.crm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getAllCustomers(String status) {
        List<Customer> customers;
        if (status != null && !status.isEmpty()) {
            customers = customerRepository.findByStatus(status.toUpperCase());
        } else {
            customers = customerRepository.findAll();
        }
        return customers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return mapToDTO(customer);
    }

    public CustomerDTO createCustomer(CustomerDTO dto) {
        // Validation for duplicate customer checks
        if (dto.getGstNumber() != null && !dto.getGstNumber().isEmpty() && customerRepository.existsByGstNumber(dto.getGstNumber())) {
            throw new RuntimeException("Customer already exists with GST Number: " + dto.getGstNumber());
        }
        if (dto.getPan() != null && !dto.getPan().isEmpty() && customerRepository.existsByPan(dto.getPan())) {
            throw new RuntimeException("Customer already exists with PAN: " + dto.getPan());
        }
        if (dto.getCompanyName() != null && customerRepository.existsByCompanyName(dto.getCompanyName())) {
            throw new RuntimeException("Customer already exists with Company Name: " + dto.getCompanyName());
        }

        String customerCode = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Customer customer = Customer.builder()
                .customerCode(customerCode)
                .companyName(dto.getCompanyName())
                .gstNumber(dto.getGstNumber())
                .pan(dto.getPan())
                .industry(dto.getIndustry())
                .businessType(dto.getBusinessType())
                .website(dto.getWebsite())
                .primaryContact(dto.getPrimaryContact())
                .contacts(dto.getContacts() != null ? dto.getContacts() : List.of())
                .billingAddress(dto.getBillingAddress())
                .shippingAddress(dto.getShippingAddress())
                .accountManagerId(dto.getAccountManagerId())
                .accountManagerName(dto.getAccountManagerName())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Created Customer: {} - {}", saved.getCustomerCode(), saved.getCompanyName());
        return mapToDTO(saved);
    }

    public CustomerDTO updateCustomer(String id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (dto.getCompanyName() != null) customer.setCompanyName(dto.getCompanyName());
        if (dto.getGstNumber() != null) customer.setGstNumber(dto.getGstNumber());
        if (dto.getPan() != null) customer.setPan(dto.getPan());
        if (dto.getIndustry() != null) customer.setIndustry(dto.getIndustry());
        if (dto.getBusinessType() != null) customer.setBusinessType(dto.getBusinessType());
        if (dto.getWebsite() != null) customer.setWebsite(dto.getWebsite());
        if (dto.getPrimaryContact() != null) customer.setPrimaryContact(dto.getPrimaryContact());
        if (dto.getContacts() != null) customer.setContacts(dto.getContacts());
        if (dto.getBillingAddress() != null) customer.setBillingAddress(dto.getBillingAddress());
        if (dto.getShippingAddress() != null) customer.setShippingAddress(dto.getShippingAddress());
        if (dto.getAccountManagerId() != null) customer.setAccountManagerId(dto.getAccountManagerId());
        if (dto.getAccountManagerName() != null) customer.setAccountManagerName(dto.getAccountManagerName());
        if (dto.getStatus() != null) customer.setStatus(dto.getStatus());

        Customer updated = customerRepository.save(customer);
        return mapToDTO(updated);
    }

    public void deleteCustomer(String id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .companyName(customer.getCompanyName())
                .gstNumber(customer.getGstNumber())
                .pan(customer.getPan())
                .industry(customer.getIndustry())
                .businessType(customer.getBusinessType())
                .website(customer.getWebsite())
                .primaryContact(customer.getPrimaryContact())
                .contacts(customer.getContacts())
                .billingAddress(customer.getBillingAddress())
                .shippingAddress(customer.getShippingAddress())
                .accountManagerId(customer.getAccountManagerId())
                .accountManagerName(customer.getAccountManagerName())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
