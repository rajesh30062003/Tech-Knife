package com.techknife.analytics.repository;

import com.techknife.analytics.entity.SearchIndex;
import com.techknife.analytics.entity.SearchEntityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchIndexRepository extends MongoRepository<SearchIndex, String> {
    Optional<SearchIndex> findByEntityIdAndEntityType(String entityId, SearchEntityType entityType);
    List<SearchIndex> findByEntityType(SearchEntityType entityType);

    @Query("{ '$or': [ " +
            "{ 'title': { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'description': { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'tags': { '$regex': ?0, '$options': 'i' } } " +
            "] }")
    List<SearchIndex> searchByKeyword(String keyword);

    @Query("{ 'entityType': { '$in': ?1 }, '$or': [ " +
            "{ 'title': { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'description': { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'tags': { '$regex': ?0, '$options': 'i' } } " +
            "] }")
    List<SearchIndex> searchByKeywordAndEntityTypes(String keyword, List<SearchEntityType> entityTypes);
    
    void deleteByEntityIdAndEntityType(String entityId, SearchEntityType entityType);
}
