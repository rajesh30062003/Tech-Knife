package com.techknife.payroll.repository;

import com.techknife.payroll.entity.Bonus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusRepository extends MongoRepository<Bonus, String> {
    List<Bonus> findByEmployeeId(String employeeId);
}
