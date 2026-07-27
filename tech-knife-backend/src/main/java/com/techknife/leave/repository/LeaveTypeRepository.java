package com.techknife.leave.repository;

import com.techknife.leave.entity.LeaveType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends MongoRepository<LeaveType, String> {
    Optional<LeaveType> findByCode(String code);
    List<LeaveType> findByActiveTrue();
    boolean existsByCode(String code);
}
