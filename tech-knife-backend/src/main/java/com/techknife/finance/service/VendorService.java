package com.techknife.finance.service;

import com.techknife.finance.dto.VendorDTO;

import java.util.List;

public interface VendorService {

    List<VendorDTO> getAllVendors();

    VendorDTO getVendorById(String id);

    VendorDTO createVendor(VendorDTO dto);

    VendorDTO updateVendor(String id, VendorDTO dto);

    void deleteVendor(String id);
}
