package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.KnowledgeBase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeBaseRepository extends MongoRepository<KnowledgeBase, String> {

    List<KnowledgeBase> findByIsPublishedTrue();

    List<KnowledgeBase> findByCategoryIdAndIsPublishedTrue(String categoryId);

    List<KnowledgeBase> findByIsPopularTrueAndIsPublishedTrue();

    Optional<KnowledgeBase> findBySlugAndIsPublishedTrue(String slug);

    List<KnowledgeBase> findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrContentContainingIgnoreCase(String query1, String query2, String query3);
}
