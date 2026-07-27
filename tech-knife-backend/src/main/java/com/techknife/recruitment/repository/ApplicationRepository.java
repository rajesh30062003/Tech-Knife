package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByCandidateId(String candidateId);

    List<Application> findByJobPostingId(String jobPostingId);

    List<Application> findByJobPostingIdAndStatus(String jobPostingId, String status);

    List<Application> findByStatus(String status);

    Optional<Application> findByCandidateIdAndJobPostingId(String candidateId, String jobPostingId);
}
