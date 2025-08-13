package br.com.hadryan.api.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByAccountId(UUID accountId, Pageable pageable);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT COUNT(o) > 0 FROM orders o WHERE o.customer.id = :customerId " +
            "AND o.orderStatus IN ('RECEIVED', 'LOADED')")
    boolean hasActiveOrders(Long customerId);

}
