package com.techknife.project.resource.service;

import com.techknife.project.resource.dto.ResourceAllocationDTO;
import com.techknife.project.resource.entity.ResourceAllocation;
import com.techknife.project.resource.repository.ResourceAllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceAllocationService {

    private final ResourceAllocationRepository allocationRepository;

    public ResourceAllocationDTO createOrUpdateAllocation(ResourceAllocationDTO dto) {
        double capacity = dto.getTotalCapacityHours() != null && dto.getTotalCapacityHours() > 0 ? dto.getTotalCapacityHours() : 160.0;
        double assigned = dto.getAssignedHours() != null ? dto.getAssignedHours() : 0.0;
        double utilization = (assigned / capacity) * 100.0;

        boolean over = utilization > 100.0;
        boolean under = utilization < 70.0;

        ResourceAllocation allocation = allocationRepository.findByEmployeeIdAndProjectId(dto.getEmployeeId(), dto.getProjectId())
                .orElseGet(() -> ResourceAllocation.builder()
                        .employeeId(dto.getEmployeeId())
                        .projectId(dto.getProjectId())
                        .build());

        allocation.setStartDate(dto.getStartDate());
        allocation.setEndDate(dto.getEndDate());
        allocation.setTotalCapacityHours(capacity);
        allocation.setAssignedHours(assigned);
        allocation.setUtilizationPercentage(utilization);
        allocation.setOverAllocated(over);
        allocation.setUnderAllocated(under);

        ResourceAllocation saved = allocationRepository.save(allocation);
        return mapToDTO(saved);
    }

    public List<ResourceAllocationDTO> getAllocationsByProject(String projectId) {
        return allocationRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ResourceAllocationDTO> getAllocationsByEmployee(String employeeId) {
        return allocationRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ResourceAllocationDTO> getAllAllocations() {
        return allocationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ResourceAllocationDTO mapToDTO(ResourceAllocation r) {
        return ResourceAllocationDTO.builder()
                .id(r.getId())
                .employeeId(r.getEmployeeId())
                .projectId(r.getProjectId())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .totalCapacityHours(r.getTotalCapacityHours())
                .assignedHours(r.getAssignedHours())
                .utilizationPercentage(r.getUtilizationPercentage())
                .overAllocated(r.isOverAllocated())
                .underAllocated(r.isUnderAllocated())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
