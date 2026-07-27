package com.techknife.leave.repository;

import com.techknife.leave.entity.LeavePolicy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeavePolicyRepository extends MongoRepository<LeavePolicy, String> {
    Optional<LeavePolicy> findByCode(String code);
    List<LeavePolicy> findByLeaveTypeId(String leaveTypeId);
    List<LeavePolicy> findByActiveTrue();
    boolean existsByCode(String code);
}
