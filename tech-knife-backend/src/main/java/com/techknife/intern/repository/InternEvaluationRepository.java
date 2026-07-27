package com.techknife.intern.repository;

import com.techknife.intern.entity.InternEvaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternEvaluationRepository extends MongoRepository<InternEvaluation, String> {
    List<InternEvaluation> findByInternId(String internId);
    List<InternEvaluation> findByEvaluatorId(String evaluatorId);
}
