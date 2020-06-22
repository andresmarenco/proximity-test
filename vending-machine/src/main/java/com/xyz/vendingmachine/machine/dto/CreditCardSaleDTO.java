package com.xyz.vendingmachine.machine.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;

/**
 * DTO for a credit card sale
 * @author amarenco
 */
@Data
public class CreditCardSaleDTO {
    @NotBlank(message = "The name must be provided")
    private String name;

    @NotBlank(message = "The credit card number must be provided")
    private String number;

    @NotBlank(message = "The security code must be provided")
    private String securityCode;

    @NotNull(message = "The expiration date must be provided")
    private YearMonth expirationDate;
}
