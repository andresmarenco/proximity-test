package com.xyz.vendingmachine.machine.dto;

import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.CashType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * DTO for {@link Cash} entities
 */
@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class CashDTO {
    @Min(message = "The denomination must be greater than 0", value = 1)
    private int denomination;

    @Min(message = "The quantity must be greater than 0", value = 1)
    private int quantity;
    private CashType type;
}
