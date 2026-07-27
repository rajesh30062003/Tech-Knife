package com.techknife.leave.service;

import com.techknife.leave.dto.WFHRequestCreateDTO;
import com.techknife.leave.dto.WFHRequestDTO;
import com.techknife.leave.entity.WFHStatus;

import java.util.List;

public interface WorkFromHomeService {
    WFHRequestDTO applyWFH(WFHRequestCreateDTO dto);
    WFHRequestDTO approveOrRejectWFH(String id, WFHStatus status, String approverId, String approverName, String comments);
    WFHRequestDTO cancelWFH(String id, String employeeId);
    WFHRequestDTO getWFHById(String id);
    List<WFHRequestDTO> getEmployeeWFHRequests(String employeeId);
    List<WFHRequestDTO> getPendingWFHForApprover(String approverId);
    List<WFHRequestDTO> getAllWFHRequests();
}
