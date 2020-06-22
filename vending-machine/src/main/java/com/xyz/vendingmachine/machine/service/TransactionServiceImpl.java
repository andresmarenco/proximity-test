package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.client.CreditCardProcessorClient;
import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardReceiptDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.CashTransaction;
import com.xyz.vendingmachine.machine.model.CreditCardTransaction;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.Transaction;
import com.xyz.vendingmachine.machine.repository.DailyTransactionsQuantity;
import com.xyz.vendingmachine.machine.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default implementation of {@link TransactionService}
 * @author amarenco
 */
@Service("transactionService")
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CreditCardProcessorClient creditCardProcessorClient;

    @Autowired
    private CashService cashService;


    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll(Sort.by("timestamp"));
    }


    @Transactional
    @Override
    public Map<Cash, AtomicInteger> process(Item item, int quantity, List<CashDTO> cashSale) throws InvalidSaleException {
        BigDecimal total = item.getCost().multiply(BigDecimal.valueOf(quantity));
        BigDecimal cash = cashSale.stream()
                .map(c -> cashService.getCashValue(c.getDenomination(), c.getType()).multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cashSale.forEach(c -> cashService.addCash(c.getDenomination(), c.getType(), c.getQuantity()));
        Map<Cash, AtomicInteger> result = cashService.changeCoins(total, cash);

        CashTransaction transaction = new CashTransaction();
        transaction.setItem(item);
        transaction.setQuantity(quantity);
        transaction.setTotal(total);

        transactionRepository.save(transaction);

        return result;
    }


    @Transactional
    @Override
    public CreditCardReceiptDTO process(Item item, int quantity, CreditCardSaleDTO creditCardSale) throws InvalidSaleException {
        log.info("Calling external credit card processor...");

        BigDecimal total = item.getCost().multiply(BigDecimal.valueOf(quantity));

        CreditCardReceiptDTO receiptDTO = new CreditCardReceiptDTO();
        receiptDTO.setAuthorizationCode(creditCardProcessorClient.processPayment(total, creditCardSale));
        receiptDTO.setTotal(total);
        receiptDTO.setTimestamp(LocalDateTime.now());

        CreditCardTransaction transaction = new CreditCardTransaction();
        transaction.setItem(item);
        transaction.setQuantity(quantity);
        transaction.setTotal(total);

        transactionRepository.save(transaction);

        return receiptDTO;
    }


    @Override
    public BigDecimal getTotalSales() {
        return transactionRepository.getTotalSales();
    }


    @Override
    public long getDailyTransactionsQuantity(LocalDate day) {
        return transactionRepository.getDailyTransactionsQuantity(day);
    }


    @Override
    public List<DailyTransactionsQuantity> getDailyTransactionsQuantity() {
        return transactionRepository.getDailyTransactionsQuantity();
    }
}
