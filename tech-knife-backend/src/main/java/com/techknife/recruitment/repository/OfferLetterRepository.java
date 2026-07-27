package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.OfferLetter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferLetterRepository extends MongoRepository<OfferLetter, String> {

    Optional<OfferLetter> findByApplicationId(String applicationId);

    List<OfferLetter> findByCandidateId(String candidateId);

    List<OfferLetter> findByJobPostingId(String jobPostingId);

    List<OfferLetter> findByAcceptanceStatus(String acceptanceStatus);
}
