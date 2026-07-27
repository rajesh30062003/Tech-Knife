package com.techknife.attendance.repository;

import com.techknife.attendance.entity.MonthlyAttendanceSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyAttendanceSummaryRepository extends MongoRepository<MonthlyAttendanceSummary, String> {

    Optional<MonthlyAttendanceSummary> findByEmployeeIdAndYearAndMonth(String employeeId, Integer year, Integer month);

    List<MonthlyAttendanceSummary> findByYearAndMonth(Integer year, Integer month);

    List<MonthlyAttendanceSummary> findByDepartmentIdAndYearAndMonth(String departmentId, Integer year, Integer month);
}
