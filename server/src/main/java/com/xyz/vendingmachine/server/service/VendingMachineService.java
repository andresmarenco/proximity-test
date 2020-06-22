package com.xyz.vendingmachine.server.service;

import com.xyz.vendingmachine.server.dto.VendingMachineStatusDTO;
import com.xyz.vendingmachine.server.model.VendingMachine;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service definition for {@link VendingMachine} entities
 * @author amarenco
 */
public interface VendingMachineService {
    /**
     * @return a list of all registered vending machines and their status
     */
    List<VendingMachineStatusDTO> getAll();

    /**
     * @param id the id of the machine
     * @return the machine or {@link Optional#empty()}
     */
    Optional<VendingMachine> findById(UUID id);

    /**
     * @param machine the machine to update
     * @param balance the new balance
     */
    void updateBalance(VendingMachine machine, BigDecimal balance);

    /**
     * @param ip the remote IP address
     * @param port the remote port
     * @return the registered machine
     */
    VendingMachine registerMachine(String ip, int port);

    /**
     * @param ip the remote IP address
     * @param port the remote port
     * @return the machine or {@link Optional#empty()}
     */
    Optional<VendingMachine> findByRemoteAddress(String ip, int port);

    /**
     * @param machine the machine to open
     */
    void openMachine(VendingMachine machine);
}
