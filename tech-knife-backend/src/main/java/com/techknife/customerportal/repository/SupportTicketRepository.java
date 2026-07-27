package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.SupportTicket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends MongoRepository<SupportTicket, String> {

    List<SupportTicket> findByCustomerAccountId(String customerAccountId);

    List<SupportTicket> findByCustomerAccountIdAndStatus(String customerAccountId, String status);

    Optional<SupportTicket> findByIdAndCustomerAccountId(String id, String customerAccountId);

    Optional<SupportTicket> findByTicketNumber(String ticketNumber);

    long countByCustomerAccountIdAndStatus(String customerAccountId, String status);

    long countByCustomerAccountId(String customerAccountId);

    List<SupportTicket> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String desc);
}
