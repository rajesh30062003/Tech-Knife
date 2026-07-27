package com.techknife.communication.repository;

import com.techknife.communication.entity.AnnouncementCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementCategoryRepository extends MongoRepository<AnnouncementCategory, String> {
    List<AnnouncementCategory> findByActiveTrue();
}
