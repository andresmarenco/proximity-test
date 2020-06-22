package com.xyz.vendingmachine.machine.client;

import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Mock implementation of {@link CreditCardProcessorClient}
 * @author amarenco
 */
@Component
@Primary
@Profile("test")
public class CreditCardProcessorClientMock implements CreditCardProcessorClient {
    @Getter
    private boolean accept = true;

    @Override
    public int processPayment(BigDecimal total, CreditCardSaleDTO creditCard) throws InvalidSaleException {
        if (accept) {
            return 1 + new Random().nextInt(500000);
        } else {
            throw new InvalidSaleException("Payment rejected");
        }
    }
}
