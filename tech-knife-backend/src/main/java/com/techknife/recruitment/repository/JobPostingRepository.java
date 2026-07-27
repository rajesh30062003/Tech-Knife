package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends MongoRepository<JobPosting, String> {

    Optional<JobPosting> findByJobCode(String jobCode);

    List<JobPosting> findByStatus(String status);

    List<JobPosting> findByDepartment(String department);

    List<JobPosting> findByTitleContainingIgnoreCase(String title);

    List<JobPosting> findByDepartmentAndStatus(String department, String status);
}
