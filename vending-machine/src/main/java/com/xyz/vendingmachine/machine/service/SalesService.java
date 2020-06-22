package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardReceiptDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.repository.DailyTransactionsQuantity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service definition for the sales of the machine
 * @author amarenco
 */
public interface SalesService {
    /**
     * @return the total sales of the machine
     */
    BigDecimal getTotalSales();

    /**
     * @param day the day
     * @return the number transactions for the given day
     */
    long getDailyTransactionsQuantity(LocalDate day);

    /**
     * @return the number of daily transactions
     */
    List<DailyTransactionsQuantity> getDailyTransactionsQuantity();

    /**
     * Sells an item by cash
     * @param code the code of the item
     * @param quantity the quantity of the items
     * @param cash the cash
     * @return the changed coins
     * @throws InvalidSaleException if the transaction could not be processed
     */
    Map<Cash, AtomicInteger> sellItem(String code, int quantity, List<CashDTO> cash) throws InvalidSaleException;

    /**
     * Sells an item by credit card
     * @param code the code of the item
     * @param quantity the quantity of the items
     * @param creditCard the credit card information
     * @return the receipt for the transaction
     * @throws InvalidSaleException if the transaction could not be processed
     */
    CreditCardReceiptDTO sellItem(String code, int quantity, CreditCardSaleDTO creditCard) throws InvalidSaleException;
}
