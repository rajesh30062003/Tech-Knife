package com.techknife.analytics.repository;

import com.techknife.analytics.entity.ExecutiveRole;
import com.techknife.analytics.entity.ExecutiveScorecard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutiveScorecardRepository extends MongoRepository<ExecutiveScorecard, String> {
    Optional<ExecutiveScorecard> findByRoleAndPeriod(ExecutiveRole role, String period);
    List<ExecutiveScorecard> findByRole(ExecutiveRole role);
}
