package com.techknife.storage;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriveFileRecordRepository extends MongoRepository<DriveFileRecord, String> {
    List<DriveFileRecord> findByProjectCodeOrderByUploadedAtDesc(String projectCode);
    List<DriveFileRecord> findByCategoryOrderByUploadedAtDesc(String category);
    Optional<DriveFileRecord> findByFileId(String fileId);
}
