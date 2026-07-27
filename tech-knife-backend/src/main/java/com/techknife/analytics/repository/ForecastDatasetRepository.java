package com.techknife.analytics.repository;

import com.techknife.analytics.entity.ForecastDataset;
import com.techknife.analytics.entity.ForecastType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastDatasetRepository extends MongoRepository<ForecastDataset, String> {
    Optional<ForecastDataset> findByForecastType(ForecastType forecastType);
    List<ForecastDataset> findByForecastTypeIn(List<ForecastType> types);
}
