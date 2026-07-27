package com.techknife.analytics.repository;

import com.techknife.analytics.entity.UniversalSearch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversalSearchRepository extends MongoRepository<UniversalSearch, String> {
    List<UniversalSearch> findTop10BySearchedByOrderBySearchedAtDesc(String searchedBy);
}
