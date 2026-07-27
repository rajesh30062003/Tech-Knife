package com.techknife.leave.service;

import com.techknife.leave.dto.LeaveTypeDTO;

import java.util.List;

public interface LeaveTypeService {
    LeaveTypeDTO createLeaveType(LeaveTypeDTO dto);
    LeaveTypeDTO updateLeaveType(String id, LeaveTypeDTO dto);
    LeaveTypeDTO getLeaveTypeById(String id);
    LeaveTypeDTO getLeaveTypeByCode(String code);
    List<LeaveTypeDTO> getAllActiveLeaveTypes();
    List<LeaveTypeDTO> getAllLeaveTypes();
    void deleteLeaveType(String id);
}
