package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.EmployeeOnboarding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeOnboardingRepository extends MongoRepository<EmployeeOnboarding, String> {

    Optional<EmployeeOnboarding> findByCandidateId(String candidateId);

    Optional<EmployeeOnboarding> findByApplicationId(String applicationId);

    List<EmployeeOnboarding> findByOnboardingStatus(String onboardingStatus);
}
