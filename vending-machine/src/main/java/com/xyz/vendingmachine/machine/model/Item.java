package com.xyz.vendingmachine.machine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
 * Item JPA Entity
 * @author amarenco
 */
@Entity
@Table(name = "item")
@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String name;

    @Column
    private BigDecimal cost;
}
