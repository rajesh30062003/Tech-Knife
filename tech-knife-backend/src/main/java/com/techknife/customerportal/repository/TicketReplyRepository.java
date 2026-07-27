package com.techknife.customerportal.repository;

import com.techknife.customerportal.entity.TicketReply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository extends MongoRepository<TicketReply, String> {

    List<TicketReply> findByTicketIdOrderByCreatedAtAsc(String ticketId);
}
