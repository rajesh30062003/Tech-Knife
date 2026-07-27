package com.techknife.intern.repository;

import com.techknife.intern.entity.InternMentor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternMentorRepository extends MongoRepository<InternMentor, String> {
    List<InternMentor> findByInternId(String internId);
    List<InternMentor> findByMentorIdAndActiveTrue(String mentorId);
    Optional<InternMentor> findByInternIdAndActiveTrue(String internId);
    long countByMentorIdAndActiveTrue(String mentorId);
}
