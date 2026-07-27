package com.techknife.asset.repository;

import com.techknife.asset.entity.AssetMaintenance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetMaintenanceRepository extends MongoRepository<AssetMaintenance, String> {
    List<AssetMaintenance> findByAssetId(String assetId);
    List<AssetMaintenance> findByStatus(String status);
}
