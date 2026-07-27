package com.techknife.asset.repository;

import com.techknife.asset.entity.AssetWarranty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetWarrantyRepository extends MongoRepository<AssetWarranty, String> {
    Optional<AssetWarranty> findByAssetId(String assetId);
    List<AssetWarranty> findByStatus(String status);
}
