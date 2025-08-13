package br.com.hadryan.api.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph("Order.complete")
    Page<Order> findAllByAccountId(Pageable pageable, UUID accountId);

    @EntityGraph("Order.withCustomer")
    @Query("SELECT o FROM orders o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerIdWithCustomer(Long customerId);

    @EntityGraph(attributePaths = {"customer", "field"})
    Optional<Order> findWithRelationsById(Long id);

}
