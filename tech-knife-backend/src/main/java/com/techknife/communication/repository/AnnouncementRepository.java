package com.techknife.communication.repository;

import com.techknife.communication.entity.Announcement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends MongoRepository<Announcement, String> {
    List<Announcement> findByStatusOrderByPublishedAtDesc(String status);
    List<Announcement> findByCategoryId(String categoryId);
}
