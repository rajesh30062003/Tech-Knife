package com.techknife.asset.repository;

import com.techknife.asset.entity.AssetMovement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetMovementRepository extends MongoRepository<AssetMovement, String> {
    List<AssetMovement> findByAssetId(String assetId);
}
