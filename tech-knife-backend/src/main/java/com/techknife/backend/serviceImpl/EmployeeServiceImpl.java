package com.techknife.backend.serviceImpl;

import com.techknife.backend.dto.CreateEmployeeRequest;
import com.techknife.backend.dto.EmployeeResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.dto.UpdateEmployeeRequest;
import com.techknife.backend.entity.Employee;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.mapper.EmployeeMapper;
import com.techknife.backend.repository.EmployeeRepository;
import com.techknife.backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID '" + request.getEmployeeId() + "' already exists");
        }

        if (employeeRepository.existsByOfficialEmail(request.getOfficialEmail())) {
            throw new BadRequestException("Official Email '" + request.getOfficialEmail() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(request);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employeeMapper.updateEntityFromRequest(request, employee);
        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByOfficialEmail(String officialEmail) {
        Employee employee = employeeRepository.findByOfficialEmail(officialEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "officialEmail", officialEmail));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, String departmentId, String managerId, String status) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? 10 : Math.min(size, 1000);

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // 1. Search Filter (firstName, lastName, employeeId, officialEmail, departmentId, designationId)
        if (search != null && !search.trim().isEmpty()) {
            String term = search.trim();
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(term, "i"),
                    Criteria.where("lastName").regex(term, "i"),
                    Criteria.where("employeeId").regex(term, "i"),
                    Criteria.where("officialEmail").regex(term, "i"),
                    Criteria.where("departmentId").regex(term, "i"),
                    Criteria.where("designationId").regex(term, "i")
            );
            criteriaList.add(searchCriteria);
        }

        // 2. DepartmentId Filter (ALL, empty, or null means NO FILTER)
        if (departmentId != null && !departmentId.trim().isEmpty() && !"ALL".equalsIgnoreCase(departmentId.trim())) {
            criteriaList.add(Criteria.where("departmentId").is(departmentId.trim()));
        }

        // 3. ManagerId Filter (ALL, empty, or null means NO FILTER)
        if (managerId != null && !managerId.trim().isEmpty() && !"ALL".equalsIgnoreCase(managerId.trim())) {
            criteriaList.add(Criteria.where("managerId").is(managerId.trim()));
        }

        // 4. Status Filter (ALL, empty, or null means NO FILTER)
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            String statusStr = status.trim();
            try {
                Employee.EmployeeStatus empStatus = Employee.EmployeeStatus.fromString(statusStr);
                criteriaList.add(new Criteria().orOperator(
                        Criteria.where("status").is(empStatus),
                        Criteria.where("status").is(statusStr),
                        Criteria.where("status").regex("^" + statusStr + "$", "i")
                ));
            } catch (Exception e) {
                criteriaList.add(Criteria.where("status").regex("^" + statusStr + "$", "i"));
            }
        }

        // 5. Strict Segregation: EXCLUDE Interns from Employees Directory
        criteriaList.add(Criteria.where("employmentType").ne("INTERN"));
        criteriaList.add(Criteria.where("employeeId").not().regex("^INT-", "i"));

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Requirement 17: Structured logging before query execution
        long startTime = System.currentTimeMillis();
        log.info("Endpoint: GET /api/employees - Parameters: [page={}, size={}, search='{}', departmentId='{}', managerId='{}', status='{}'] - Mongo Query: {}",
                safePage, safeSize, search, departmentId, managerId, status, query.getQueryObject());

        long totalElements = 0;
        try {
            totalElements = mongoTemplate.count(query, Employee.class);
        } catch (Exception e) {
            log.error("Failed to count employees in MongoDB: ", e);
        }

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        query.with(pageable);

        List<Employee> rawEmployees;
        try {
            rawEmployees = mongoTemplate.find(query, Employee.class);
        } catch (Exception e) {
            log.error("Mongo query for employees failed with exception: ", e);
            rawEmployees = Collections.emptyList();
        }

        long executionTime = System.currentTimeMillis() - startTime;
        log.info("GET /api/employees Query Executed - Returned count: {}, Total elements: {}, Execution time: {} ms",
                rawEmployees.size(), totalElements, executionTime);

        // Requirements 13, 14, 15: Safe mapping, skipping malformed documents without HTTP 500
        List<EmployeeResponse> content = new ArrayList<>();
        for (Employee emp : rawEmployees) {
            if (emp == null) continue;
            try {
                EmployeeResponse res = employeeMapper.toResponse(emp);
                if (res != null) {
                    content.add(res);
                }
            } catch (Exception ex) {
                log.warn("Skipping malformed employee document (ID: {}): {}", emp.getId(), ex.getMessage());
            }
        }

        int totalPages = safeSize > 0 ? (int) Math.ceil((double) totalElements / safeSize) : 1;
        if (totalPages == 0) totalPages = 1;
        boolean isLast = safePage >= (totalPages - 1);

        return PagedResponse.<EmployeeResponse>builder()
                .content(content)
                .page(safePage)
                .size(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(isLast)
                .build();
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployeeStatus(String id, Employee.EmployeeStatus status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setStatus(status);
        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponse(updated);
    }
}
