package com.techknife.intern.repository;

import com.techknife.intern.entity.InternAttendanceSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternAttendanceSummaryRepository extends MongoRepository<InternAttendanceSummary, String> {
    List<InternAttendanceSummary> findByInternId(String internId);
    Optional<InternAttendanceSummary> findByInternIdAndMonthYear(String internId, String monthYear);
}
