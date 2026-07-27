package com.techknife.procurement.service.impl;

import com.techknife.procurement.dto.SupplierDTO;
import com.techknife.procurement.entity.Supplier;
import com.techknife.procurement.repository.SupplierRepository;
import com.techknife.procurement.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierDTO getSupplierById(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));
        return mapToDTO(supplier);
    }

    @Override
    public SupplierDTO createSupplier(SupplierDTO dto) {
        if (supplierRepository.existsBySupplierCode(dto.getSupplierCode())) {
            throw new IllegalArgumentException("Supplier already exists with code: " + dto.getSupplierCode());
        }

        Supplier supplier = Supplier.builder()
                .supplierCode(dto.getSupplierCode())
                .companyName(dto.getCompanyName())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .gstNumber(dto.getGstNumber())
                .panNumber(dto.getPanNumber())
                .address(dto.getAddress())
                .rating(dto.getRating() != null ? dto.getRating() : 5.0)
                .outstandingBalance(dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : BigDecimal.ZERO)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Supplier saved = supplierRepository.save(supplier);
        return mapToDTO(saved);
    }

    @Override
    public SupplierDTO updateSupplier(String id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));

        if (dto.getCompanyName() != null) supplier.setCompanyName(dto.getCompanyName());
        if (dto.getContactPerson() != null) supplier.setContactPerson(dto.getContactPerson());
        if (dto.getEmail() != null) supplier.setEmail(dto.getEmail());
        if (dto.getPhone() != null) supplier.setPhone(dto.getPhone());
        if (dto.getGstNumber() != null) supplier.setGstNumber(dto.getGstNumber());
        if (dto.getPanNumber() != null) supplier.setPanNumber(dto.getPanNumber());
        if (dto.getAddress() != null) supplier.setAddress(dto.getAddress());
        if (dto.getRating() != null) supplier.setRating(dto.getRating());
        if (dto.getOutstandingBalance() != null) supplier.setOutstandingBalance(dto.getOutstandingBalance());
        if (dto.getStatus() != null) supplier.setStatus(dto.getStatus());

        Supplier saved = supplierRepository.save(supplier);
        return mapToDTO(saved);
    }

    @Override
    public void deleteSupplier(String id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierDTO mapToDTO(Supplier s) {
        return SupplierDTO.builder()
                .id(s.getId())
                .supplierCode(s.getSupplierCode())
                .companyName(s.getCompanyName())
                .contactPerson(s.getContactPerson())
                .email(s.getEmail())
                .phone(s.getPhone())
                .gstNumber(s.getGstNumber())
                .panNumber(s.getPanNumber())
                .address(s.getAddress())
                .rating(s.getRating())
                .outstandingBalance(s.getOutstandingBalance())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .createdBy(s.getCreatedBy())
                .updatedBy(s.getUpdatedBy())
                .build();
    }
}
