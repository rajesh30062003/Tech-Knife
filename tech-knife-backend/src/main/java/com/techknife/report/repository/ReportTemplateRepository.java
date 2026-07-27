package com.techknife.report.repository;

import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.entity.ReportTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportTemplateRepository extends MongoRepository<ReportTemplate, String> {
    Optional<ReportTemplate> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByNameIgnoreCase(String name);
    List<ReportTemplate> findByCategory(ReportCategoryType category);
    List<ReportTemplate> findByNameContainingIgnoreCase(String name);
}
