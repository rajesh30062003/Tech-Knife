package com.techknife.asset.repository;

import com.techknife.asset.entity.MaintenanceSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends MongoRepository<MaintenanceSchedule, String> {
    List<MaintenanceSchedule> findByAssetId(String assetId);
    List<MaintenanceSchedule> findByStatus(String status);
}
