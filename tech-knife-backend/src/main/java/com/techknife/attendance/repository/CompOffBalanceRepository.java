package com.techknife.attendance.repository;

import com.techknife.attendance.entity.CompOffBalance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompOffBalanceRepository extends MongoRepository<CompOffBalance, String> {

    Optional<CompOffBalance> findByEmployeeId(String employeeId);
}
