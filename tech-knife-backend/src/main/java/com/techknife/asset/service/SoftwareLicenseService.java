package com.techknife.asset.service;

import com.techknife.asset.dto.LicenseAssignmentDTO;
import com.techknife.asset.dto.SoftwareLicenseDTO;

import java.util.List;

public interface SoftwareLicenseService {
    SoftwareLicenseDTO createLicense(SoftwareLicenseDTO dto);
    SoftwareLicenseDTO updateLicense(String id, SoftwareLicenseDTO dto);
    List<SoftwareLicenseDTO> getAllLicenses();
    SoftwareLicenseDTO getLicenseById(String id);
    LicenseAssignmentDTO assignLicense(String licenseId, String employeeId, String employeeName);
    LicenseAssignmentDTO revokeLicense(String licenseId, String employeeId);
    List<LicenseAssignmentDTO> getLicenseAssignments(String licenseId);
}
