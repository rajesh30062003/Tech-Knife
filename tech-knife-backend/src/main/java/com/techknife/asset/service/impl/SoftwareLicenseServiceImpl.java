package com.techknife.asset.service.impl;

import com.techknife.asset.dto.LicenseAssignmentDTO;
import com.techknife.asset.dto.SoftwareLicenseDTO;
import com.techknife.asset.entity.LicenseAssignment;
import com.techknife.asset.entity.SoftwareLicense;
import com.techknife.asset.repository.LicenseAssignmentRepository;
import com.techknife.asset.repository.SoftwareLicenseRepository;
import com.techknife.asset.service.SoftwareLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoftwareLicenseServiceImpl implements SoftwareLicenseService {

    private final SoftwareLicenseRepository licenseRepository;
    private final LicenseAssignmentRepository licenseAssignmentRepository;

    @Override
    public SoftwareLicenseDTO createLicense(SoftwareLicenseDTO dto) {
        if (licenseRepository.existsByLicenseKey(dto.getLicenseKey())) {
            throw new IllegalArgumentException("Software license already exists with key: " + dto.getLicenseKey());
        }

        SoftwareLicense license = SoftwareLicense.builder()
                .licenseKey(dto.getLicenseKey())
                .softwareName(dto.getSoftwareName())
                .vendor(dto.getVendor())
                .purchaseDate(dto.getPurchaseDate())
                .expiryDate(dto.getExpiryDate())
                .seatsPurchased(dto.getSeatsPurchased() != null ? dto.getSeatsPurchased() : 1)
                .seatsUsed(0)
                .cost(dto.getCost())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .notes(dto.getNotes())
                .assignedEmployeeIds(new ArrayList<>())
                .assignedEmployeeNames(new ArrayList<>())
                .build();

        SoftwareLicense saved = licenseRepository.save(license);
        return mapToDTO(saved);
    }

    @Override
    public SoftwareLicenseDTO updateLicense(String id, SoftwareLicenseDTO dto) {
        SoftwareLicense license = licenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Software license not found with id: " + id));

        if (dto.getSoftwareName() != null) license.setSoftwareName(dto.getSoftwareName());
        if (dto.getVendor() != null) license.setVendor(dto.getVendor());
        if (dto.getPurchaseDate() != null) license.setPurchaseDate(dto.getPurchaseDate());
        if (dto.getExpiryDate() != null) license.setExpiryDate(dto.getExpiryDate());
        if (dto.getSeatsPurchased() != null) license.setSeatsPurchased(dto.getSeatsPurchased());
        if (dto.getCost() != null) license.setCost(dto.getCost());
        if (dto.getStatus() != null) license.setStatus(dto.getStatus());
        if (dto.getNotes() != null) license.setNotes(dto.getNotes());

        SoftwareLicense saved = licenseRepository.save(license);
        return mapToDTO(saved);
    }

    @Override
    public List<SoftwareLicenseDTO> getAllLicenses() {
        return licenseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SoftwareLicenseDTO getLicenseById(String id) {
        SoftwareLicense license = licenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Software license not found with id: " + id));
        return mapToDTO(license);
    }

    @Override
    public LicenseAssignmentDTO assignLicense(String licenseId, String employeeId, String employeeName) {
        SoftwareLicense license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Software license not found with id: " + licenseId));

        if (license.getSeatsUsed() >= license.getSeatsPurchased()) {
            throw new IllegalArgumentException("All seats for license key " + license.getLicenseKey() + " are currently occupied");
        }

        if (license.getAssignedEmployeeIds().contains(employeeId)) {
            throw new IllegalArgumentException("License already assigned to employee ID: " + employeeId);
        }

        LicenseAssignment assignment = LicenseAssignment.builder()
                .licenseId(license.getId())
                .softwareName(license.getSoftwareName())
                .employeeId(employeeId)
                .employeeName(employeeName)
                .assignedDate(LocalDate.now())
                .status("ACTIVE")
                .build();

        LicenseAssignment savedAssignment = licenseAssignmentRepository.save(assignment);

        license.getAssignedEmployeeIds().add(employeeId);
        if (employeeName != null) license.getAssignedEmployeeNames().add(employeeName);
        license.setSeatsUsed(license.getSeatsUsed() + 1);
        licenseRepository.save(license);

        return mapToAssignmentDTO(savedAssignment);
    }

    @Override
    public LicenseAssignmentDTO revokeLicense(String licenseId, String employeeId) {
        SoftwareLicense license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Software license not found with id: " + licenseId));

        LicenseAssignment assignment = licenseAssignmentRepository.findByLicenseIdAndEmployeeIdAndStatus(licenseId, employeeId, "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException("Active license assignment not found for employee: " + employeeId));

        assignment.setStatus("REVOKED");
        LicenseAssignment savedAssignment = licenseAssignmentRepository.save(assignment);

        license.getAssignedEmployeeIds().remove(employeeId);
        if (assignment.getEmployeeName() != null) license.getAssignedEmployeeNames().remove(assignment.getEmployeeName());
        license.setSeatsUsed(Math.max(0, license.getSeatsUsed() - 1));
        licenseRepository.save(license);

        return mapToAssignmentDTO(savedAssignment);
    }

    @Override
    public List<LicenseAssignmentDTO> getLicenseAssignments(String licenseId) {
        return licenseAssignmentRepository.findByLicenseId(licenseId).stream()
                .map(this::mapToAssignmentDTO)
                .collect(Collectors.toList());
    }

    private SoftwareLicenseDTO mapToDTO(SoftwareLicense l) {
        return SoftwareLicenseDTO.builder()
                .id(l.getId())
                .licenseKey(l.getLicenseKey())
                .softwareName(l.getSoftwareName())
                .vendor(l.getVendor())
                .purchaseDate(l.getPurchaseDate())
                .expiryDate(l.getExpiryDate())
                .seatsPurchased(l.getSeatsPurchased())
                .seatsUsed(l.getSeatsUsed())
                .cost(l.getCost())
                .status(l.getStatus())
                .assignedEmployeeIds(l.getAssignedEmployeeIds())
                .assignedEmployeeNames(l.getAssignedEmployeeNames())
                .notes(l.getNotes())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }

    private LicenseAssignmentDTO mapToAssignmentDTO(LicenseAssignment a) {
        return LicenseAssignmentDTO.builder()
                .id(a.getId())
                .licenseId(a.getLicenseId())
                .softwareName(a.getSoftwareName())
                .employeeId(a.getEmployeeId())
                .employeeName(a.getEmployeeName())
                .assignedDate(a.getAssignedDate())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
