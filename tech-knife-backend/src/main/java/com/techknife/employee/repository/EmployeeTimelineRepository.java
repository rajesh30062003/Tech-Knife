package com.techknife.employee.repository;

import com.techknife.employee.entity.EmployeeTimeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTimelineRepository extends MongoRepository<EmployeeTimeline, String> {
    List<EmployeeTimeline> findByEmployeeIdOrderByTimestampDesc(String employeeId);
    List<EmployeeTimeline> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);
    Page<EmployeeTimeline> findByEmployeeId(String employeeId, Pageable pageable);
}

