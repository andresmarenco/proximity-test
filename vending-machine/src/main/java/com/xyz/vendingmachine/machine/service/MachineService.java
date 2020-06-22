package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.exception.ClosedMachineException;
import com.xyz.vendingmachine.machine.exception.LockedMachineException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service definition for the physical state of the machine
 * @author amarenco
 */
public interface MachineService {
    /**
     * Opens the machine
     * @param code the security code
     * @throws IllegalArgumentException if the security code is invalid
     * @throws LockedMachineException if the machine is locked
     */
    void open(String code) throws IllegalArgumentException, LockedMachineException;

    /**
     * Closes the machine
     */
    void close();

    /**
     * @return <code>true</code> if the machine is open
     */
    boolean isOpen();

    /**
     * @param code the code of the location
     * @param quantity the quantity of items to dispatch
     * @return <code>true</code> if the item was dispatched
     */
    boolean dispatchItem(String code, int quantity);

    /**
     * @return the current amount of cash in the machine
     */
    BigDecimal getCurrentCashAmount();

    /**
     * Removes the cash currently on the machine
     * @return a list with all the cash retrieved from the machine
     * @throws ClosedMachineException if the machine is closed
     */
    List<CashDTO> retrieveCash() throws ClosedMachineException;

    /**
     * Sets the quantity of coins of the given denomination
     * @param denomination the denomination of the coin
     * @param quantity the quantity of coins
     * @throws IllegalArgumentException if the data is incorrect
     * @throws ClosedMachineException if the machine is closed
     */
    void setCoins(int denomination, int quantity) throws IllegalArgumentException, ClosedMachineException;
}
