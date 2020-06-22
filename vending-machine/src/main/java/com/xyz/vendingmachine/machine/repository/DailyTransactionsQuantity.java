package com.xyz.vendingmachine.machine.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Projection of the total of daily transactions query
 * @author amarenco
 */
public interface DailyTransactionsQuantity {
    LocalDate getDay();
    Long getQuantity();
}
