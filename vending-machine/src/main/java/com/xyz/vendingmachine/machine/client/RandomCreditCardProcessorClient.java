package com.xyz.vendingmachine.machine.client;

import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Credit card processor that randomly accepts/rejects the payments
 * @author amarenco
 */
@Component("creditCardProcessorClient")
public class RandomCreditCardProcessorClient implements CreditCardProcessorClient {

    @Override
    public int processPayment(BigDecimal total, CreditCardSaleDTO creditCard) throws InvalidSaleException {
        Random random = new Random();
        if (random.nextBoolean()) {
            return 1 + random.nextInt(500000);
        } else {
            throw new InvalidSaleException("Payment rejected");
        }
    }
}
