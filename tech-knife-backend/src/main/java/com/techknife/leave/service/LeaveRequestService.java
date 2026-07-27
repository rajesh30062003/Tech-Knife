package com.techknife.leave.service;

import com.techknife.leave.dto.LeaveApprovalDTO;
import com.techknife.leave.dto.LeaveRequestCreateDTO;
import com.techknife.leave.dto.LeaveRequestDTO;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequestDTO applyLeave(LeaveRequestCreateDTO dto);
    LeaveRequestDTO approveOrRejectLeave(String requestId, LeaveApprovalDTO approvalDTO, String approverId, String approverName, String approverRole);
    LeaveRequestDTO cancelLeave(String requestId, String employeeId, String reason);
    LeaveRequestDTO getLeaveRequestById(String id);
    List<LeaveRequestDTO> getEmployeeLeaveRequests(String employeeId);
    List<LeaveRequestDTO> getPendingApprovalsForUser(String approverId);
    List<LeaveRequestDTO> getDepartmentLeaveRequests(String departmentId);
    List<LeaveRequestDTO> getAllLeaveRequests();
}
