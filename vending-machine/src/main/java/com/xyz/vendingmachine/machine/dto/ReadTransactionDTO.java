package com.xyz.vendingmachine.machine.dto;

import com.xyz.vendingmachine.machine.model.CashTransaction;
import com.xyz.vendingmachine.machine.model.PaymentMethod;
import com.xyz.vendingmachine.machine.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for a single transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadTransactionDTO {
    private String id;
    private LocalDateTime timestamp;
    private String item;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal total;
    private PaymentMethod paymentMethod;


    /**
     * @param transaction the transaction
     */
    public ReadTransactionDTO(Transaction transaction) {
        this.id = transaction.getId().toString();
        this.timestamp = transaction.getTimestamp();
        this.quantity = transaction.getQuantity();
        this.total = transaction.getTotal();
        this.paymentMethod = transaction instanceof CashTransaction ? PaymentMethod.CASH : PaymentMethod.CREDIT_CARD;

        if (transaction.getItem() != null) {
            this.item = transaction.getItem().getName();
            this.unitPrice = transaction.getItem().getCost();
        }
    }
}
