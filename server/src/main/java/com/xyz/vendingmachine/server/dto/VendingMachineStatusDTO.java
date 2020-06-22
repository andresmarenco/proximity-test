package com.xyz.vendingmachine.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xyz.vendingmachine.server.model.VendingMachine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for the status of the vending machines
 */
@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class VendingMachineStatusDTO {
    @JsonIgnore
    private VendingMachine machine;

    private boolean active;
    private boolean collect;

    /**
     * @param machine the vending machine
     */
    public VendingMachineStatusDTO(VendingMachine machine) {
        this.machine = machine;
        this.active = false;
    }

    /**
     * @return the id of the {@link #machine}
     */
    public String getId() {
        return machine != null ? machine.getId().toString() : null;
    }

    /**
     * @return the remote IP address of the {@link #machine}
     */
    public String getRemoteAddress() {
        return machine != null ? machine.getRemoteAddress() : null;
    }

    /**
     * @return the port of the {@link #machine}
     */
    public Integer getPort() {
        return machine != null ? machine.getPort() : null;
    }

    /**
     * @return the sales total of the {@link #machine}
     */
    public BigDecimal getBalance() {
        return machine != null ? machine.getBalance() : null;
    }
}
