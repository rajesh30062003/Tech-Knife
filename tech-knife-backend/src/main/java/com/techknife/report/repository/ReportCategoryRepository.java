package com.techknife.report.repository;

import com.techknife.report.entity.ReportCategory;
import com.techknife.report.entity.ReportCategoryType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportCategoryRepository extends MongoRepository<ReportCategory, String> {
    Optional<ReportCategory> findByCategoryType(ReportCategoryType categoryType);
    boolean existsByCategoryType(ReportCategoryType categoryType);
}
