package com.xyz.vendingmachine.machine.repository;

import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.CashType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for {@link Cash} entities
 * @author amarenco
 */
@Repository
public interface CashRepository extends JpaRepository<Cash, Integer> {
    /**
     * Clears all the cash from the machine
     */
    @Modifying
    @Query("UPDATE Cash c SET c.quantity = 0")
    void clearCash();

    /**
     * @return a list of all the cash of the given type
     */
    List<Cash> findByType(CashType type);
}
