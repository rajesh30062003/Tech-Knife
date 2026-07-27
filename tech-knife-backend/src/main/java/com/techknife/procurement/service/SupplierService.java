package com.techknife.procurement.service;

import com.techknife.procurement.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {
    List<SupplierDTO> getAllSuppliers();
    SupplierDTO getSupplierById(String id);
    SupplierDTO createSupplier(SupplierDTO dto);
    SupplierDTO updateSupplier(String id, SupplierDTO dto);
    void deleteSupplier(String id);
}
