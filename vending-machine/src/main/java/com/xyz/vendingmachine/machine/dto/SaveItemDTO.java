package com.xyz.vendingmachine.machine.dto;

import com.xyz.vendingmachine.machine.model.Item;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * DTO for creating/updating {@link Item} entities
 * @author amarenco
 */
@Data
public class SaveItemDTO {
    @NotBlank(message = "The name of the item must be provided")
    private String name;

    @DecimalMin(message = "The cost must be greater than 0", value = "0.0", inclusive = false)
    private BigDecimal cost;

    /**
     * @return a new {@link Item} with the current data
     */
    public Item toItem() {
        return toItem(new Item());
    }

    /**
     * @param item the base item
     * @return the give item with updated fields
     */
    public Item toItem(Item item) {
        item.setName(name);
        item.setCost(cost);

        return item;
    }
}
