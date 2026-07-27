package com.techknife.leave.service;

import com.techknife.leave.dto.LeavePolicyDTO;

import java.util.List;

public interface LeavePolicyService {
    LeavePolicyDTO createPolicy(LeavePolicyDTO dto);
    LeavePolicyDTO updatePolicy(String id, LeavePolicyDTO dto);
    LeavePolicyDTO getPolicyById(String id);
    LeavePolicyDTO getPolicyByCode(String code);
    List<LeavePolicyDTO> getPoliciesByLeaveType(String leaveTypeId);
    List<LeavePolicyDTO> getAllActivePolicies();
    List<LeavePolicyDTO> getAllPolicies();
    void deletePolicy(String id);
}
