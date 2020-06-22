package com.xyz.vendingmachine.machine.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for a receipt after a credit card payment
 * @author amarenco
 */
@Data
public class CreditCardReceiptDTO {
    private int authorizationCode;
    private LocalDateTime timestamp;
    private BigDecimal total;
}
