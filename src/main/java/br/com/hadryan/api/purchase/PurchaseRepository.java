package br.com.hadryan.api.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @EntityGraph("Purchase.withItems")
    @Query("SELECT p FROM Purchase p WHERE p.account.id = :accountId")
    Page<Purchase> findAllByAccountWithItems(Pageable page, UUID accountId);

    @EntityGraph("Purchase.withItemsAndProducts")
    @Query("SELECT p FROM Purchase p WHERE p.account.id = :accountId")
    Page<Purchase> findAllByAccountWithItemsAndProducts(Pageable page, UUID accountId);

    @EntityGraph("Purchase.full")
    @Query("SELECT p FROM Purchase p WHERE p.id = :id AND p.account.id = :accountId")
    Optional<Purchase> findFullPurchaseById(Long id, UUID accountId);
}
