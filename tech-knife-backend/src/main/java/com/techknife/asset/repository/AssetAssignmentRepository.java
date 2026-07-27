package com.techknife.asset.repository;

import com.techknife.asset.entity.AssetAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetAssignmentRepository extends MongoRepository<AssetAssignment, String> {
    List<AssetAssignment> findByAssetId(String assetId);
    List<AssetAssignment> findByEmployeeId(String employeeId);
    Optional<AssetAssignment> findByAssetIdAndStatus(String assetId, String status);
}
