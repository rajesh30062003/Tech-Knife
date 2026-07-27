package com.techknife.holiday.repository;

import com.techknife.holiday.entity.RestrictedHolidayRequest;
import com.techknife.leave.entity.LeaveStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestrictedHolidayRequestRepository extends MongoRepository<RestrictedHolidayRequest, String> {
    List<RestrictedHolidayRequest> findByEmployeeId(String employeeId);
    List<RestrictedHolidayRequest> findByEmployeeIdAndYear(String employeeId, Integer year);
    List<RestrictedHolidayRequest> findByApproverIdAndStatus(String approverId, LeaveStatus status);
    List<RestrictedHolidayRequest> findByStatus(LeaveStatus status);
}
