package com.xyz.vendingmachine.server.model;

import com.netflix.appinfo.InstanceInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA entity for a Vending Machine
 * @author amarenco
 */
@Entity
@Table(name = "vending_machine")
@Data
@NoArgsConstructor
public class VendingMachine {
    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String remoteAddress;

    @Column
    private int port;

    @Column
    private BigDecimal balance;


    /**
     * @param instanceInfo the instance info
     */
    public VendingMachine(InstanceInfo instanceInfo) {
        this.remoteAddress = instanceInfo.getIPAddr();
        this.port = instanceInfo.getPort();
        this.balance = BigDecimal.ZERO;
    }
}
