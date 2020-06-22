package com.xyz.vendingmachine.machine.repository;

import com.xyz.vendingmachine.machine.model.OpenAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for {@link OpenAttempt} entities
 * @author amarenco
 */
@Repository
public interface OpenAttemptRepository extends JpaRepository<OpenAttempt, UUID> {
    /**
     * @param pageable the page request
     * @return the latest open attempt events
     */
    @Query("SELECT a FROM OpenAttempt a ORDER BY a.timestamp DESC")
    List<OpenAttempt> getLatestAttempts(Pageable pageable);
}
