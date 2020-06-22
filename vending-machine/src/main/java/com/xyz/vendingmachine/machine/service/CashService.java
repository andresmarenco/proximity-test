package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.CashType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service definition for {@link Cash} entities
 * @author amarenco
 */
public interface CashService {
    /**
     * @return a list with all the cash in the machine
     */
    List<CashDTO> getAll();

    /**
     * Clears all the cash from the machine
     */
    void clearCash();

    /**
     * @return the total amount of cash in the machine
     */
    BigDecimal countCash();

    /**
     * Adds cash in the machine
     * @param denomination the denomination of the cash
     * @param type the type of the cash
     * @param quantity the quantity of cash to add of the same denomination
     * @throws IllegalArgumentException if the data is incorrect
     */
    void addCash(int denomination, CashType type, int quantity) throws IllegalArgumentException;

    /**
     * @param denomination the denomination of the cash
     * @param type the type of the cash
     * @return <code>true</code> if the denomination is valid
     */
    boolean isValidCash(int denomination, CashType type);

    /**
     * @param denomination the denomination of the cash
     * @param type the type of the cash
     * @return the real value of the cash
     */
    BigDecimal getCashValue(int denomination, CashType type);

    /**
     * Sets the quantity of coins of the given denomination
     * @param denomination the denomination of the coin
     * @param quantity the quantity of coins
     * @throws IllegalArgumentException if the data is incorrect
     */
    void setCoins(int denomination, int quantity) throws IllegalArgumentException;

    /**
     * Dispenses the correct change for the total and the given cash
     * @param total the total to pay
     * @param cash the payed cash
     * @return the changed coins
     * @throws InvalidSaleException if the total could not be changed
     */
    Map<Cash, AtomicInteger> changeCoins(BigDecimal total, BigDecimal cash) throws InvalidSaleException;
}
