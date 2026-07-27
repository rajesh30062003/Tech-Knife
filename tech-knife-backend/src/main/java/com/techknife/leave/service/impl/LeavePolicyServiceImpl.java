package com.techknife.leave.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.leave.dto.LeavePolicyDTO;
import com.techknife.leave.entity.LeavePolicy;
import com.techknife.leave.repository.LeavePolicyRepository;
import com.techknife.leave.service.LeavePolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeavePolicyServiceImpl implements LeavePolicyService {

    private final LeavePolicyRepository leavePolicyRepository;

    @Override
    @Transactional
    public LeavePolicyDTO createPolicy(LeavePolicyDTO dto) {
        if (leavePolicyRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Leave Policy code already exists: " + dto.getCode());
        }

        LeavePolicy policy = LeavePolicy.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .leaveTypeId(dto.getLeaveTypeId())
                .departmentId(dto.getDepartmentId())
                .designationId(dto.getDesignationId())
                .branchId(dto.getBranchId())
                .employmentType(dto.getEmploymentType())
                .gender(dto.getGender())
                .annualQuota(dto.getAnnualQuota())
                .maxConsecutiveDays(dto.getMaxConsecutiveDays())
                .minNoticeDays(dto.getMinNoticeDays())
                .allowHalfDay(dto.getAllowHalfDay() == null || dto.getAllowHalfDay())
                .active(dto.getActive() == null || dto.getActive())
                .build();

        LeavePolicy saved = leavePolicyRepository.save(policy);
        log.info("Created LeavePolicy: ID={}, Code={}", saved.getId(), saved.getCode());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public LeavePolicyDTO updatePolicy(String id, LeavePolicyDTO dto) {
        LeavePolicy policy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Policy not found with ID: " + id));

        if (dto.getName() != null) policy.setName(dto.getName());
        if (dto.getLeaveTypeId() != null) policy.setLeaveTypeId(dto.getLeaveTypeId());
        if (dto.getDepartmentId() != null) policy.setDepartmentId(dto.getDepartmentId());
        if (dto.getDesignationId() != null) policy.setDesignationId(dto.getDesignationId());
        if (dto.getBranchId() != null) policy.setBranchId(dto.getBranchId());
        if (dto.getEmploymentType() != null) policy.setEmploymentType(dto.getEmploymentType());
        if (dto.getGender() != null) policy.setGender(dto.getGender());
        if (dto.getAnnualQuota() != null) policy.setAnnualQuota(dto.getAnnualQuota());
        if (dto.getMaxConsecutiveDays() != null) policy.setMaxConsecutiveDays(dto.getMaxConsecutiveDays());
        if (dto.getMinNoticeDays() != null) policy.setMinNoticeDays(dto.getMinNoticeDays());
        if (dto.getAllowHalfDay() != null) policy.setAllowHalfDay(dto.getAllowHalfDay());
        if (dto.getActive() != null) policy.setActive(dto.getActive());

        LeavePolicy updated = leavePolicyRepository.save(policy);
        log.info("Updated LeavePolicy: ID={}, Code={}", updated.getId(), updated.getCode());
        return mapToDTO(updated);
    }

    @Override
    public LeavePolicyDTO getPolicyById(String id) {
        return leavePolicyRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Policy not found with ID: " + id));
    }

    @Override
    public LeavePolicyDTO getPolicyByCode(String code) {
        return leavePolicyRepository.findByCode(code.toUpperCase())
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Policy not found with Code: " + code));
    }

    @Override
    public List<LeavePolicyDTO> getPoliciesByLeaveType(String leaveTypeId) {
        return leavePolicyRepository.findByLeaveTypeId(leaveTypeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeavePolicyDTO> getAllActivePolicies() {
        return leavePolicyRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeavePolicyDTO> getAllPolicies() {
        return leavePolicyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePolicy(String id) {
        if (!leavePolicyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave Policy not found with ID: " + id);
        }
        leavePolicyRepository.deleteById(id);
        log.info("Deleted LeavePolicy ID={}", id);
    }

    private LeavePolicyDTO mapToDTO(LeavePolicy entity) {
        return LeavePolicyDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .leaveTypeId(entity.getLeaveTypeId())
                .departmentId(entity.getDepartmentId())
                .designationId(entity.getDesignationId())
                .branchId(entity.getBranchId())
                .employmentType(entity.getEmploymentType())
                .gender(entity.getGender())
                .annualQuota(entity.getAnnualQuota())
                .maxConsecutiveDays(entity.getMaxConsecutiveDays())
                .minNoticeDays(entity.getMinNoticeDays())
                .allowHalfDay(entity.getAllowHalfDay())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
