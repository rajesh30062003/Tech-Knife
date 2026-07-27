package com.techknife.leave.service;

import com.techknife.leave.dto.LeaveBalanceDTO;

import java.util.List;

public interface LeaveBalanceService {
    List<LeaveBalanceDTO> initializeEmployeeBalances(String employeeId, Integer year);
    List<LeaveBalanceDTO> getEmployeeBalances(String employeeId, Integer year);
    LeaveBalanceDTO getEmployeeLeaveBalance(String employeeId, String leaveTypeId, Integer year);
    LeaveBalanceDTO adjustBalance(String employeeId, String leaveTypeId, Integer year, Double additionalDays, String reason);
    void processCarryForward(String employeeId, Integer fromYear, Integer toYear);
}
