package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.SharedDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedDocumentRepository extends MongoRepository<SharedDocument, String> {

    List<SharedDocument> findByCustomerAccountId(String customerAccountId);

    List<SharedDocument> findByCustomerAccountIdAndCategory(String customerAccountId, String category);

    List<SharedDocument> findByProjectId(String projectId);

    Optional<SharedDocument> findByIdAndCustomerAccountId(String id, String customerAccountId);

    List<SharedDocument> findByCustomerAccountIdAndDocumentNameContainingIgnoreCase(String customerAccountId, String query);
}
