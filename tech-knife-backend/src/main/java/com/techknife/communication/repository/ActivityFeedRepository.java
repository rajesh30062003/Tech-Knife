package com.techknife.communication.repository;

import com.techknife.communication.entity.ActivityFeed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityFeedRepository extends MongoRepository<ActivityFeed, String> {
    List<ActivityFeed> findTop50ByOrderByCreatedAtDesc();
    List<ActivityFeed> findByActorIdOrderByCreatedAtDesc(String actorId);
    List<ActivityFeed> findByModuleOrderByCreatedAtDesc(String module);
}
