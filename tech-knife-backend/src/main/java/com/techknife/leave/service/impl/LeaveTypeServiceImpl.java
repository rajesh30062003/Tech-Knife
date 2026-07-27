package com.techknife.leave.service.impl;

import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.leave.dto.LeaveTypeDTO;
import com.techknife.leave.entity.LeaveType;
import com.techknife.leave.repository.LeaveTypeRepository;
import com.techknife.leave.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    @Transactional
    public LeaveTypeDTO createLeaveType(LeaveTypeDTO dto) {
        if (leaveTypeRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Leave type code already exists: " + dto.getCode());
        }

        LeaveType leaveType = LeaveType.builder()
                .code(dto.getCode().toUpperCase())
                .name(dto.getName())
                .description(dto.getDescription())
                .defaultAnnualQuota(dto.getDefaultAnnualQuota() != null ? dto.getDefaultAnnualQuota() : 12.0)
                .carryForwardAllowed(Boolean.TRUE.equals(dto.getCarryForwardAllowed()))
                .maxCarryForwardDays(dto.getMaxCarryForwardDays() != null ? dto.getMaxCarryForwardDays() : 0.0)
                .encashable(Boolean.TRUE.equals(dto.getEncashable()))
                .requiresAttachment(Boolean.TRUE.equals(dto.getRequiresAttachment()))
                .active(dto.getActive() == null || dto.getActive())
                .build();

        LeaveType saved = leaveTypeRepository.save(leaveType);
        log.info("Created LeaveType: ID={}, Code={}", saved.getId(), saved.getCode());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public LeaveTypeDTO updateLeaveType(String id, LeaveTypeDTO dto) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found with ID: " + id));

        if (dto.getName() != null) leaveType.setName(dto.getName());
        if (dto.getDescription() != null) leaveType.setDescription(dto.getDescription());
        if (dto.getDefaultAnnualQuota() != null) leaveType.setDefaultAnnualQuota(dto.getDefaultAnnualQuota());
        if (dto.getCarryForwardAllowed() != null) leaveType.setCarryForwardAllowed(dto.getCarryForwardAllowed());
        if (dto.getMaxCarryForwardDays() != null) leaveType.setMaxCarryForwardDays(dto.getMaxCarryForwardDays());
        if (dto.getEncashable() != null) leaveType.setEncashable(dto.getEncashable());
        if (dto.getRequiresAttachment() != null) leaveType.setRequiresAttachment(dto.getRequiresAttachment());
        if (dto.getActive() != null) leaveType.setActive(dto.getActive());

        LeaveType updated = leaveTypeRepository.save(leaveType);
        log.info("Updated LeaveType: ID={}, Code={}", updated.getId(), updated.getCode());
        return mapToDTO(updated);
    }

    @Override
    public LeaveTypeDTO getLeaveTypeById(String id) {
        return leaveTypeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found with ID: " + id));
    }

    @Override
    public LeaveTypeDTO getLeaveTypeByCode(String code) {
        return leaveTypeRepository.findByCode(code.toUpperCase())
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType not found with Code: " + code));
    }

    @Override
    public List<LeaveTypeDTO> getAllActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveTypeDTO> getAllLeaveTypes() {
        return leaveTypeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLeaveType(String id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("LeaveType not found with ID: " + id);
        }
        leaveTypeRepository.deleteById(id);
        log.info("Deleted LeaveType ID={}", id);
    }

    private LeaveTypeDTO mapToDTO(LeaveType entity) {
        return LeaveTypeDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .defaultAnnualQuota(entity.getDefaultAnnualQuota())
                .carryForwardAllowed(entity.getCarryForwardAllowed())
                .maxCarryForwardDays(entity.getMaxCarryForwardDays())
                .encashable(entity.getEncashable())
                .requiresAttachment(entity.getRequiresAttachment())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
