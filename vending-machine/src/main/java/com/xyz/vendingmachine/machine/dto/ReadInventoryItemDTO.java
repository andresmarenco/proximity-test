package com.xyz.vendingmachine.machine.dto;

import com.xyz.vendingmachine.machine.model.StorageLocation;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO to display an item in the inventory
 * @author amarenco
 */
@Data
public class ReadInventoryItemDTO {
    private String code;
    private String name;
    private BigDecimal cost;
    private int quantity;


    /**
     * @param storageLocation the data with the item
     */
    public ReadInventoryItemDTO(StorageLocation storageLocation) {
        this.code = storageLocation.getCode();
        this.quantity = storageLocation.getQuantity();

        if (storageLocation.getItem() != null) {
            this.name = storageLocation.getItem().getName();
            this.cost = storageLocation.getItem().getCost();
        }
    }
}
