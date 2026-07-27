package com.techknife.attendance.repository;

import com.techknife.attendance.entity.CompOffGrant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompOffGrantRepository extends MongoRepository<CompOffGrant, String> {

    List<CompOffGrant> findByEmployeeId(String employeeId);

    List<CompOffGrant> findByEmployeeIdAndStatus(String employeeId, String status);

    List<CompOffGrant> findByStatus(String status);

    List<CompOffGrant> findByStatusAndExpiryDateBefore(String status, LocalDate date);
}
