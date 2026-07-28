package com.techknife.backend.repository;

import com.techknife.backend.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository("backendAttendanceRepository")
public interface AttendanceRepository extends MongoRepository<Attendance, String> {


    Optional<Attendance> findByUserIdAndDate(String userId, LocalDate date);

    List<Attendance> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByUserId(String userId);

    Page<Attendance> findByDepartment(String department, Pageable pageable);

    Page<Attendance> findByDateBetweenAndDepartment(LocalDate startDate, LocalDate endDate, String department, Pageable pageable);
}
