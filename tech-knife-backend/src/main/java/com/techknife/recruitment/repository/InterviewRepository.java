package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.Interview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends MongoRepository<Interview, String> {

    List<Interview> findByApplicationId(String applicationId);

    List<Interview> findByCandidateId(String candidateId);

    List<Interview> findByJobPostingId(String jobPostingId);

    List<Interview> findByResult(String result);
}
