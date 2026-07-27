package com.techknife.attendance.repository;

import com.techknife.attendance.entity.AttendanceRecord;
import com.techknife.attendance.entity.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends MongoRepository<AttendanceRecord, String> {

    Optional<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);

    List<AttendanceRecord> findByDate(LocalDate date);

    List<AttendanceRecord> findByDateAndStatus(LocalDate date, AttendanceStatus status);

    List<AttendanceRecord> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByDepartmentIdAndDate(String departmentId, LocalDate date);

    Page<AttendanceRecord> findByDepartmentId(String departmentId, Pageable pageable);

    Page<AttendanceRecord> findByEmployeeId(String employeeId, Pageable pageable);

    long countByDateAndStatus(LocalDate date, AttendanceStatus status);

    long countByDateAndIsLateTrue(LocalDate date);

    long countByDateAndIsEarlyExitTrue(LocalDate date);

    List<AttendanceRecord> findByDateAndIsLateTrue(LocalDate date);

    List<AttendanceRecord> findByDateAndIsEarlyExitTrue(LocalDate date);

    List<AttendanceRecord> findByDateAndOvertimeHoursGreaterThan(LocalDate date, Double hours);
}
