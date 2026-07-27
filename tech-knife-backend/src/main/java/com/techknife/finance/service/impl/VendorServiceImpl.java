package com.techknife.finance.service.impl;

import com.techknife.finance.dto.VendorDTO;
import com.techknife.finance.entity.Vendor;
import com.techknife.finance.repository.VendorRepository;
import com.techknife.finance.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    public List<VendorDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VendorDTO getVendorById(String id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found with id: " + id));
        return mapToDTO(vendor);
    }

    @Override
    public VendorDTO createVendor(VendorDTO dto) {
        if (vendorRepository.existsByVendorCode(dto.getVendorCode())) {
            throw new IllegalArgumentException("Vendor code already exists: " + dto.getVendorCode());
        }

        if (dto.getGstNumber() != null && !dto.getGstNumber().isBlank() && vendorRepository.existsByGstNumber(dto.getGstNumber())) {
            throw new IllegalArgumentException("Vendor with GST Number " + dto.getGstNumber() + " already exists!");
        }

        if (dto.getPanNumber() != null && !dto.getPanNumber().isBlank() && vendorRepository.existsByPanNumber(dto.getPanNumber())) {
            throw new IllegalArgumentException("Vendor with PAN Number " + dto.getPanNumber() + " already exists!");
        }

        Vendor vendor = Vendor.builder()
                .vendorCode(dto.getVendorCode())
                .vendorName(dto.getVendorName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .gstNumber(dto.getGstNumber())
                .panNumber(dto.getPanNumber())
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .ifscCode(dto.getIfscCode())
                .branchName(dto.getBranchName())
                .outstandingBalance(dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : BigDecimal.ZERO)
                .totalPurchases(dto.getTotalPurchases() != null ? dto.getTotalPurchases() : BigDecimal.ZERO)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Vendor saved = vendorRepository.save(vendor);
        return mapToDTO(saved);
    }

    @Override
    public VendorDTO updateVendor(String id, VendorDTO dto) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found with id: " + id));

        if (dto.getGstNumber() != null && !dto.getGstNumber().isBlank() && vendorRepository.existsByGstNumberAndIdNot(dto.getGstNumber(), id)) {
            throw new IllegalArgumentException("GST Number " + dto.getGstNumber() + " is already associated with another vendor!");
        }

        if (dto.getPanNumber() != null && !dto.getPanNumber().isBlank() && vendorRepository.existsByPanNumberAndIdNot(dto.getPanNumber(), id)) {
            throw new IllegalArgumentException("PAN Number " + dto.getPanNumber() + " is already associated with another vendor!");
        }

        if (dto.getVendorName() != null) vendor.setVendorName(dto.getVendorName());
        if (dto.getEmail() != null) vendor.setEmail(dto.getEmail());
        if (dto.getPhone() != null) vendor.setPhone(dto.getPhone());
        if (dto.getAddress() != null) vendor.setAddress(dto.getAddress());
        if (dto.getGstNumber() != null) vendor.setGstNumber(dto.getGstNumber());
        if (dto.getPanNumber() != null) vendor.setPanNumber(dto.getPanNumber());
        if (dto.getBankName() != null) vendor.setBankName(dto.getBankName());
        if (dto.getAccountNumber() != null) vendor.setAccountNumber(dto.getAccountNumber());
        if (dto.getIfscCode() != null) vendor.setIfscCode(dto.getIfscCode());
        if (dto.getBranchName() != null) vendor.setBranchName(dto.getBranchName());
        if (dto.getStatus() != null) vendor.setStatus(dto.getStatus());

        Vendor saved = vendorRepository.save(vendor);
        return mapToDTO(saved);
    }

    @Override
    public void deleteVendor(String id) {
        if (!vendorRepository.existsById(id)) {
            throw new IllegalArgumentException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }

    private VendorDTO mapToDTO(Vendor v) {
        return VendorDTO.builder()
                .id(v.getId())
                .vendorCode(v.getVendorCode())
                .vendorName(v.getVendorName())
                .email(v.getEmail())
                .phone(v.getPhone())
                .address(v.getAddress())
                .gstNumber(v.getGstNumber())
                .panNumber(v.getPanNumber())
                .bankName(v.getBankName())
                .accountNumber(v.getAccountNumber())
                .ifscCode(v.getIfscCode())
                .branchName(v.getBranchName())
                .outstandingBalance(v.getOutstandingBalance())
                .totalPurchases(v.getTotalPurchases())
                .status(v.getStatus())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .createdBy(v.getCreatedBy())
                .updatedBy(v.getUpdatedBy())
                .build();
    }
}
