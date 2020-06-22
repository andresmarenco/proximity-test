package com.xyz.vendingmachine.server.repository;

import com.xyz.vendingmachine.server.model.VendingMachine;
import com.xyz.vendingmachine.server.service.VendingMachineService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for {@link VendingMachine} entities
 * @author amarenco
 */
@Repository
public interface VendingMachineRepository extends JpaRepository<VendingMachine, UUID> {
    /**
     * @param remoteAddress the remote IP of the machine
     * @param port the port of the machine
     * @return the machine or {@link Optional#empty()}
     */
    Optional<VendingMachine> findByRemoteAddressAndPort(String remoteAddress, int port);
}
