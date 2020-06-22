package com.xyz.vendingmachine.machine.client;

import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;

import java.math.BigDecimal;

/**
 * Client for an external credit card processor
 * @author amarenco
 */
public interface CreditCardProcessorClient {
    /**
     * @param total the total amount to pay
     * @param creditCard the credit card information
     * @return the authorization code
     * @throws InvalidSaleException if the payment was not processed
     */
    int processPayment(BigDecimal total, CreditCardSaleDTO creditCard) throws InvalidSaleException;
}
