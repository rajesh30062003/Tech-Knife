package com.techknife.employee.repository;

import com.techknife.employee.entity.BloodGroup;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link EmployeeSearchRepository} utilizing {@link MongoTemplate}
 * to dynamically construct criteria queries for advanced searching, filtering, and pagination.
 */
@Repository
@RequiredArgsConstructor
public class EmployeeSearchRepositoryImpl implements EmployeeSearchRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * Executes a dynamic MongoDB query based on provided criteria filters and pageable arguments.
     *
     * @param searchTerm Keyword search across name, ID, email, and GitHub fields
     * @param departmentId Department ID filter
     * @param designationId Designation ID filter
     * @param managerId Manager ID filter
     * @param status Employee status filter
     * @param employmentType Employment type filter
     * @param bloodGroup Blood group filter
     * @param skills List of required skills filter
     * @param pageable Pagination and sorting criteria
     * @return Paginated result set of matching {@link Employee} documents
     */
    @Override
    public Page<Employee> searchEmployees(
            String searchTerm,
            String departmentId,
            String designationId,
            String managerId,
            EmployeeStatus status,
            EmploymentType employmentType,
            BloodGroup bloodGroup,
            List<String> skills,
            Pageable pageable
    ) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String regex = ".*" + searchTerm.trim() + ".*";
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(regex, "i"),
                    Criteria.where("lastName").regex(regex, "i"),
                    Criteria.where("employeeId").regex(regex, "i"),
                    Criteria.where("officialEmail").regex(regex, "i"),
                    Criteria.where("githubUsername").regex(regex, "i")
            );
            criteriaList.add(searchCriteria);
        }

        if (departmentId != null && !departmentId.trim().isEmpty()) {
            criteriaList.add(Criteria.where("departmentId").is(departmentId.trim()));
        }

        if (designationId != null && !designationId.trim().isEmpty()) {
            criteriaList.add(Criteria.where("designationId").is(designationId.trim()));
        }

        if (managerId != null && !managerId.trim().isEmpty()) {
            criteriaList.add(Criteria.where("managerId").is(managerId.trim()));
        }

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (employmentType != null) {
            criteriaList.add(Criteria.where("employmentType").is(employmentType));
        }

        if (bloodGroup != null) {
            criteriaList.add(Criteria.where("bloodGroup").is(bloodGroup));
        }

        if (skills != null && !skills.isEmpty()) {
            criteriaList.add(Criteria.where("skills").all(skills));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Employee.class);

        query.with(pageable);

        List<Employee> employees = mongoTemplate.find(query, Employee.class);

        return new PageImpl<>(employees, pageable, total);
    }
}

