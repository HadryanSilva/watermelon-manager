package br.com.hadryan.api.vendor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Page<Vendor> findByAccountId(UUID accountId, Pageable pageable);

    boolean existsByPhoneAndIdNot(String phone, Long excludeId);
}
