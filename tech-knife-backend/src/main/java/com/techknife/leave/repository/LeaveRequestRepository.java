package com.techknife.leave.repository;

import com.techknife.leave.entity.LeaveRequest;
import com.techknife.leave.entity.LeaveStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {
    List<LeaveRequest> findByEmployeeId(String employeeId);
    List<LeaveRequest> findByEmployeeIdAndStatus(String employeeId, LeaveStatus status);
    List<LeaveRequest> findByCurrentApproverIdAndStatus(String currentApproverId, LeaveStatus status);
    List<LeaveRequest> findByDepartmentIdAndStatus(String departmentId, LeaveStatus status);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    List<LeaveRequest> findByStartDateBetweenOrEndDateBetween(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2);
    List<LeaveRequest> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);
    List<LeaveRequest> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LeaveStatus status, LocalDate date1, LocalDate date2);
    List<LeaveRequest> findByStartDateGreaterThanEqualAndStatus(LocalDate startDate, LeaveStatus status);
    
    // For overlap checking
    List<LeaveRequest> findByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String employeeId, List<LeaveStatus> statuses, LocalDate endDate, LocalDate startDate);
}
