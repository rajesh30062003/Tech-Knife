package com.techknife.asset.repository;

import com.techknife.asset.entity.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {
    Optional<Asset> findByAssetCode(String assetCode);
    Optional<Asset> findBySerialNumber(String serialNumber);
    boolean existsByAssetCode(String assetCode);
    boolean existsBySerialNumber(String serialNumber);
    List<Asset> findByStatus(String status);
    List<Asset> findByCategoryId(String categoryId);
    List<Asset> findByAssignedEmployeeId(String employeeId);
}
