package com.xyz.vendingmachine.server.repository;

import com.xyz.vendingmachine.server.model.Alert;
import com.xyz.vendingmachine.server.model.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for {@link Alert} entities
 * @author amarenco
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    /**
     * @return the latest alert for the machine
     */
    Optional<Alert> findFirstByMachineOrderByTimestampDesc(VendingMachine machine);
}
