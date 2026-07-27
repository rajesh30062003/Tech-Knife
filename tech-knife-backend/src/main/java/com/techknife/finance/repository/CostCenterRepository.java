package com.techknife.finance.repository;

import com.techknife.finance.entity.CostCenter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostCenterRepository extends MongoRepository<CostCenter, String> {

    Optional<CostCenter> findByCenterCode(String centerCode);

    List<CostCenter> findByType(String type);

    boolean existsByCenterCode(String centerCode);

    List<CostCenter> findByCenterCodeContainingIgnoreCaseOrCenterNameContainingIgnoreCase(String code, String name);
}
