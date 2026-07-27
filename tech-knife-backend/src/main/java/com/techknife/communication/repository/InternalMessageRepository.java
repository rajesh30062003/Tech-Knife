package com.techknife.communication.repository;

import com.techknife.communication.entity.InternalMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalMessageRepository extends MongoRepository<InternalMessage, String> {
    List<InternalMessage> findByThreadIdOrderBySentAtAsc(String threadId);
    List<InternalMessage> findByRecipientIdsContaining(String userId);
}
