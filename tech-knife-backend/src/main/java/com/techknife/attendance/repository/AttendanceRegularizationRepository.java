package com.techknife.attendance.repository;

import com.techknife.attendance.entity.AttendanceRegularization;
import com.techknife.attendance.entity.RegularizationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRegularizationRepository extends MongoRepository<AttendanceRegularization, String> {

    List<AttendanceRegularization> findByEmployeeId(String employeeId);

    List<AttendanceRegularization> findByStatus(RegularizationStatus status);

    List<AttendanceRegularization> findByApproverIdAndStatus(String approverId, RegularizationStatus status);

    List<AttendanceRegularization> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    long countByStatus(RegularizationStatus status);
}
