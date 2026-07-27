package com.techknife.holiday.service;

import com.techknife.holiday.dto.RestrictedHolidayRequestDTO;
import com.techknife.leave.entity.LeaveStatus;

import java.util.List;

public interface RestrictedHolidayService {
    RestrictedHolidayRequestDTO applyRestrictedHoliday(RestrictedHolidayRequestDTO dto);
    RestrictedHolidayRequestDTO approveOrRejectRestrictedHoliday(String id, LeaveStatus status, String approverId, String approverName, String comments);
    RestrictedHolidayRequestDTO getRequestById(String id);
    List<RestrictedHolidayRequestDTO> getEmployeeRequests(String employeeId, Integer year);
    List<RestrictedHolidayRequestDTO> getPendingRequestsForApprover(String approverId);
    List<RestrictedHolidayRequestDTO> getAllRequests();
}
