package com.techknife.crm.repository;

import com.techknife.crm.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByCustomerCode(String customerCode);
    boolean existsByGstNumber(String gstNumber);
    boolean existsByPan(String pan);
    boolean existsByCompanyName(String companyName);
    List<Customer> findByStatus(String status);
    List<Customer> findByAccountManagerId(String accountManagerId);
    List<Customer> findByCompanyNameContainingIgnoreCaseOrGstNumberContainingIgnoreCase(String companyName, String gstNumber);
}
