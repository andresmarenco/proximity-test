package com.xyz.vendingmachine.machine.repository;

import com.xyz.vendingmachine.machine.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for {@link Transaction} entities
 * @author amarenco
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    /**
     * @return the total of sales
     */
    @Query("SELECT COALESCE(SUM(t.total), 0) FROM Transaction t")
    BigDecimal getTotalSales();

    /**
     * @return the list of summarized daily transactions
     */
    @Query(value = "SELECT CAST(sale_timestamp AS DATE) AS day, COUNT(1) AS quantity FROM transaction GROUP BY day ORDER BY day", nativeQuery = true)
    List<DailyTransactionsQuantity> getDailyTransactionsQuantity();

    /**
     * @param day the day the calculate
     * @return the list of summarized daily transactions
     */
    @Query(value = "SELECT COUNT(1) FROM transaction WHERE CAST(sale_timestamp AS DATE) = ?1", nativeQuery = true)
    long getDailyTransactionsQuantity(LocalDate day);
}
