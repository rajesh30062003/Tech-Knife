package com.techknife.backend.repository;

import com.techknife.backend.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("backendEmployeeRepository")
public interface EmployeeRepository extends MongoRepository<Employee, String> {


    Optional<Employee> findByOfficialEmail(String officialEmail);

    Optional<Employee> findByEmployeeId(String employeeId);

    List<Employee> findByStatus(Employee.EmployeeStatus status);

    Page<Employee> findByStatus(Employee.EmployeeStatus status, Pageable pageable);

    Page<Employee> findByDepartmentId(String departmentId, Pageable pageable);

    List<Employee> findByDepartmentId(String departmentId);

    Page<Employee> findByManagerId(String managerId, Pageable pageable);

    List<Employee> findByManagerId(String managerId);

    Page<Employee> findByDepartmentIdAndStatus(String departmentId, Employee.EmployeeStatus status, Pageable pageable);

    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } }, { 'employeeId': { '$regex': ?0, '$options': 'i' } }, { 'officialEmail': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<Employee> searchByName(String keyword, Pageable pageable);

    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    boolean existsByOfficialEmail(String officialEmail);

    boolean existsByEmployeeId(String employeeId);
}
