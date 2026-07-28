package com.techknife.employee.repository;

import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for {@link Employee} documents.
 * Extends {@link MongoRepository} and {@link EmployeeSearchRepository} to provide standard CRUD,
 * custom finder queries, and advanced multi-criteria search capabilities.
 */
@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String>, EmployeeSearchRepository {

    /**
     * Find an employee by official email address.
     *
     * @param officialEmail Official email address
     * @return Optional containing the matching employee, or empty if not found
     */
    Optional<Employee> findByOfficialEmail(String officialEmail);

    /**
     * Find an employee by unique employee ID (e.g. EMP-1001).
     *
     * @param employeeId Employee unique identifier
     * @return Optional containing the matching employee, or empty if not found
     */
    Optional<Employee> findByEmployeeId(String employeeId);

    /**
     * Check if an employee exists with the given official email.
     *
     * @param officialEmail Official email address
     * @return true if an employee exists with the official email, false otherwise
     */
    boolean existsByOfficialEmail(String officialEmail);

    /**
     * Check if an employee exists with the given employee ID.
     *
     * @param employeeId Employee unique identifier
     * @return true if an employee exists with the employee ID, false otherwise
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * Find all employees matching a given employment status.
     *
     * @param status Employment status
     * @return List of matching employees
     */
    List<Employee> findByStatus(EmployeeStatus status);

    /**
     * Find a paginated list of employees matching a given employment status.
     *
     * @param status Employment status
     * @param pageable Pagination and sorting criteria
     * @return Page of matching employees
     */
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    /**
     * Find a paginated list of employees belonging to a specific department.
     *
     * @param departmentId Department identifier
     * @param pageable Pagination and sorting criteria
     * @return Page of matching employees
     */
    Page<Employee> findByDepartmentId(String departmentId, Pageable pageable);

    /**
     * Find all employees belonging to a specific department.
     *
     * @param departmentId Department identifier
     * @return List of matching employees
     */
    List<Employee> findByDepartmentId(String departmentId);

    /**
     * Find a paginated list of direct reports for a given manager.
     *
     * @param managerId Manager's employee identifier
     * @param pageable Pagination and sorting criteria
     * @return Page of matching employees
     */
    Page<Employee> findByManagerId(String managerId, Pageable pageable);

    /**
     * Find all direct reports for a given manager.
     *
     * @param managerId Manager's employee identifier
     * @return List of matching employees
     */
    List<Employee> findByManagerId(String managerId);

    /**
     * Find a paginated list of employees filtered by department ID and status.
     *
     * @param departmentId Department identifier
     * @param status Employment status
     * @param pageable Pagination and sorting criteria
     * @return Page of matching employees
     */
    Page<Employee> findByDepartmentIdAndStatus(String departmentId, EmployeeStatus status, Pageable pageable);

    /**
     * Perform a case-insensitive regex search on employee first name, last name, employee ID, or official email.
     *
     * @param keyword Search keyword
     * @param pageable Pagination and sorting criteria
     * @return Page of matching employees
     */
    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } }, { 'employeeId': { '$regex': ?0, '$options': 'i' } }, { 'officialEmail': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<Employee> searchByName(String keyword, Pageable pageable);

    List<Employee> findByCompanyId(String companyId);
}


