package br.com.hadryan.api.purchase;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @EntityGraph("items")
    @Query("SELECT p FROM Purchase p WHERE p.account = :accountId")
    List<Purchase> findAllByAccountWithItems(Long accountId);

}
