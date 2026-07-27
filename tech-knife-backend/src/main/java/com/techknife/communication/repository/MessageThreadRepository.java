package com.techknife.communication.repository;

import com.techknife.communication.entity.MessageThread;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageThreadRepository extends MongoRepository<MessageThread, String> {
    List<MessageThread> findByParticipantIdsContainingOrderByLastMessageAtDesc(String userId);
}
