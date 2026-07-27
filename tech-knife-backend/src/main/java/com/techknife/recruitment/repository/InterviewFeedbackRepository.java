package com.techknife.recruitment.repository;

import com.techknife.recruitment.entity.InterviewFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewFeedbackRepository extends MongoRepository<InterviewFeedback, String> {

    List<InterviewFeedback> findByInterviewId(String interviewId);

    List<InterviewFeedback> findByInterviewerId(String interviewerId);
}
