package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.KnowledgeCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeCategoryRepository extends MongoRepository<KnowledgeCategory, String> {

    List<KnowledgeCategory> findAllByOrderByDisplayOrderAsc();
}
