package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {

    Optional<Candidate> findByCandidateCode(String candidateCode);

    Optional<Candidate> findByEmail(String email);

    List<Candidate> findByStatus(String status);

    List<Candidate> findBySkillsContainingIgnoreCase(String skill);

    List<Candidate> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
