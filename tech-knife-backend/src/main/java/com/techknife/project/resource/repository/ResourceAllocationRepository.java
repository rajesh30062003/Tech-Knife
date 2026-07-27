package com.techknife.project.resource.repository;

import com.techknife.project.resource.entity.ResourceAllocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceAllocationRepository extends MongoRepository<ResourceAllocation, String> {

    List<ResourceAllocation> findByProjectId(String projectId);

    List<ResourceAllocation> findByEmployeeId(String employeeId);

    Optional<ResourceAllocation> findByEmployeeIdAndProjectId(String employeeId, String projectId);
}
