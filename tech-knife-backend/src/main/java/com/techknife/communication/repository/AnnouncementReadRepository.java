package com.techknife.communication.repository;

import com.techknife.communication.entity.AnnouncementRead;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementReadRepository extends MongoRepository<AnnouncementRead, String> {
    Optional<AnnouncementRead> findByAnnouncementIdAndUserId(String announcementId, String userId);
    boolean existsByAnnouncementIdAndUserId(String announcementId, String userId);
    long countByAnnouncementId(String announcementId);
}
