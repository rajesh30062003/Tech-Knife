package com.techknife.leave.repository;

import com.techknife.leave.entity.LeaveBalance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends MongoRepository<LeaveBalance, String> {
    List<LeaveBalance> findByEmployeeIdAndYear(String employeeId, Integer year);
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(String employeeId, String leaveTypeId, Integer year);
    List<LeaveBalance> findByYear(Integer year);
}
