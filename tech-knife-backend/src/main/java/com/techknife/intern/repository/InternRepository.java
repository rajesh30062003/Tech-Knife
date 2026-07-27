package com.techknife.intern.repository;

import com.techknife.intern.entity.Intern;
import com.techknife.intern.entity.InternStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternRepository extends MongoRepository<Intern, String> {
    Optional<Intern> findByInternCode(String internCode);
    Optional<Intern> findByOfficialEmail(String officialEmail);
    boolean existsByInternCode(String internCode);
    boolean existsByOfficialEmail(String officialEmail);
    List<Intern> findByDepartmentId(String departmentId);
    List<Intern> findByMentorId(String mentorId);
    long countByMentorIdAndStatus(String mentorId, InternStatus status);
    Page<Intern> findByStatus(InternStatus status, Pageable pageable);

    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } }, { 'internCode': { '$regex': ?0, '$options': 'i' } }, { 'officialEmail': { '$regex': ?0, '$options': 'i' } }, { 'college': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<Intern> searchByName(String keyword, Pageable pageable);
}
