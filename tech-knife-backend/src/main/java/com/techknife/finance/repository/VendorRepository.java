package com.techknife.finance.repository;

import com.techknife.finance.entity.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends MongoRepository<Vendor, String> {

    Optional<Vendor> findByVendorCode(String vendorCode);

    boolean existsByVendorCode(String vendorCode);

    boolean existsByGstNumberAndIdNot(String gstNumber, String id);

    boolean existsByPanNumberAndIdNot(String panNumber, String id);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);

    List<Vendor> findByVendorNameContainingIgnoreCaseOrVendorCodeContainingIgnoreCaseOrGstNumberContainingIgnoreCase(
            String name, String code, String gst);
}
