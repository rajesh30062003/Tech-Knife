package com.techknife.leave.repository;

import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.entity.WorkFromHomeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkFromHomeRequestRepository extends MongoRepository<WorkFromHomeRequest, String> {
    List<WorkFromHomeRequest> findByEmployeeId(String employeeId);
    List<WorkFromHomeRequest> findByApproverIdAndStatus(String approverId, WFHStatus status);
    List<WorkFromHomeRequest> findByDepartmentIdAndStatus(String departmentId, WFHStatus status);
    List<WorkFromHomeRequest> findByStatus(WFHStatus status);
    List<WorkFromHomeRequest> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(WFHStatus status, LocalDate date1, LocalDate date2);
    List<WorkFromHomeRequest> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);
}
