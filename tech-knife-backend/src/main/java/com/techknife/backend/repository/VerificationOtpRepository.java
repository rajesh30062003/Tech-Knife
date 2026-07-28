package com.techknife.backend.repository;

import com.techknife.backend.entity.VerificationOtp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("backendVerificationOtpRepository")
public interface VerificationOtpRepository extends MongoRepository<VerificationOtp, String> {


    Optional<VerificationOtp> findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(String email, VerificationOtp.OtpType type);

    Optional<VerificationOtp> findByEmailAndOtpCodeAndTypeAndUsedFalse(String email, String otpCode, VerificationOtp.OtpType type);

    void deleteByEmailAndType(String email, VerificationOtp.OtpType type);
}
