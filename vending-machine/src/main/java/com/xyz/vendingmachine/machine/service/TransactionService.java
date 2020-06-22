package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardReceiptDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.Transaction;
import com.xyz.vendingmachine.machine.repository.DailyTransactionsQuantity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service definition for {@link Transaction} entities
 * @author amarenco
 */
public interface TransactionService {
    /**
     * @return a list of all transactions sorted by timestamp
     */
    List<Transaction> getAll();

    /**
     * @param item the item to sell
     * @param quantity the quantity of the item
     * @param cashSale the cash data
     * @return the changed coins
     * @throws InvalidSaleException if the transaction could not be processed
     */
    Map<Cash, AtomicInteger> process(Item item, int quantity, List<CashDTO> cashSale) throws InvalidSaleException;

    /**
     * @param item the item to sell
     * @param quantity the quantity of the item
     * @param creditCardSale the credit card data
     * @return the receipt for the transaction
     * @throws InvalidSaleException if the transaction could not be processed
     */
    CreditCardReceiptDTO process(Item item, int quantity, CreditCardSaleDTO creditCardSale) throws InvalidSaleException;

    /**
     * @return the total of sales
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
}
