package com.techknife.leave.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.leave.dto.LeaveBalanceDTO;
import com.techknife.leave.entity.LeaveBalance;
import com.techknife.leave.entity.LeaveType;
import com.techknife.leave.repository.LeaveBalanceRepository;
import com.techknife.leave.repository.LeaveTypeRepository;
import com.techknife.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    @Transactional
    public List<LeaveBalanceDTO> initializeEmployeeBalances(String employeeId, Integer year) {
        List<LeaveType> activeTypes = leaveTypeRepository.findByActiveTrue();
        List<LeaveBalance> initialized = new ArrayList<>();

        for (LeaveType type : activeTypes) {
            Optional<LeaveBalance> existing = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, type.getId(), year);
            if (existing.isEmpty()) {
                Double quota = type.getDefaultAnnualQuota() != null ? type.getDefaultAnnualQuota() : 12.0;
                LeaveBalance balance = LeaveBalance.builder()
                        .employeeId(employeeId)
                        .leaveTypeId(type.getId())
                        .leaveTypeName(type.getName())
                        .year(year)
                        .allocatedDays(quota)
                        .accruedDays(quota)
                        .carryForwardDays(0.0)
                        .usedDays(0.0)
                        .pendingDays(0.0)
                        .lapsedDays(0.0)
                        .availableDays(quota)
                        .build();
                initialized.add(leaveBalanceRepository.save(balance));
            } else {
                initialized.add(existing.get());
            }
        }
        log.info("Initialized {} leave balances for Employee ID={}, Year={}", initialized.size(), employeeId, year);
        return initialized.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LeaveBalanceDTO> getEmployeeBalances(String employeeId, Integer year) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year);
        if (balances.isEmpty()) {
            return initializeEmployeeBalances(employeeId, year);
        }
        return balances.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public LeaveBalanceDTO getEmployeeLeaveBalance(String employeeId, String leaveTypeId, Integer year) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseGet(() -> {
                    initializeEmployeeBalances(employeeId, year);
                    return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                            .orElseThrow(() -> new ResourceNotFoundException("Leave Balance not found for type ID: " + leaveTypeId));
                });
        return mapToDTO(balance);
    }

    @Override
    @Transactional
    public LeaveBalanceDTO adjustBalance(String employeeId, String leaveTypeId, Integer year, Double additionalDays, String reason) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Balance record not found"));

        balance.setAllocatedDays(balance.getAllocatedDays() + additionalDays);
        balance.setAvailableDays(balance.getAvailableDays() + additionalDays);

        LeaveBalance saved = leaveBalanceRepository.save(balance);
        log.info("Adjusted Leave Balance for Employee ID={}, TypeID={}, AddedDays={}, Reason={}", employeeId, leaveTypeId, additionalDays, reason);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void processCarryForward(String employeeId, Integer fromYear, Integer toYear) {
        List<LeaveBalance> previousBalances = leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, fromYear);
        for (LeaveBalance prev : previousBalances) {
            LeaveType leaveType = leaveTypeRepository.findById(prev.getLeaveTypeId()).orElse(null);
            if (leaveType != null && Boolean.TRUE.equals(leaveType.getCarryForwardAllowed())) {
                double remaining = prev.getAvailableDays();
                double maxCarry = leaveType.getMaxCarryForwardDays() != null ? leaveType.getMaxCarryForwardDays() : 0.0;
                double carryForward = Math.min(remaining, maxCarry);

                LeaveBalance nextBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveType.getId(), toYear)
                        .orElseGet(() -> {
                            Double quota = leaveType.getDefaultAnnualQuota() != null ? leaveType.getDefaultAnnualQuota() : 12.0;
                            return LeaveBalance.builder()
                                    .employeeId(employeeId)
                                    .leaveTypeId(leaveType.getId())
                                    .leaveTypeName(leaveType.getName())
                                    .year(toYear)
                                    .allocatedDays(quota)
                                    .accruedDays(quota)
                                    .usedDays(0.0)
                                    .pendingDays(0.0)
                                    .lapsedDays(0.0)
                                    .build();
                        });

                nextBalance.setCarryForwardDays(carryForward);
                nextBalance.setAvailableDays(nextBalance.getAllocatedDays() + carryForward);
                leaveBalanceRepository.save(nextBalance);
                log.info("Carried forward {} days for Employee ID={}, LeaveType={}", carryForward, employeeId, leaveType.getName());
            }
        }
    }

    private LeaveBalanceDTO mapToDTO(LeaveBalance entity) {
        return LeaveBalanceDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .leaveTypeId(entity.getLeaveTypeId())
                .leaveTypeName(entity.getLeaveTypeName())
                .year(entity.getYear())
                .allocatedDays(entity.getAllocatedDays())
                .accruedDays(entity.getAccruedDays())
                .carryForwardDays(entity.getCarryForwardDays())
                .usedDays(entity.getUsedDays())
                .pendingDays(entity.getPendingDays())
                .lapsedDays(entity.getLapsedDays())
                .availableDays(entity.getAvailableDays())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
