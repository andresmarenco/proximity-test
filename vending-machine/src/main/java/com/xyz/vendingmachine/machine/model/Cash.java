package com.xyz.vendingmachine.machine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

/**
 * Cash JPA Entity
 */
@Entity
@Table(name = "cash")
@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class Cash {
    @Id
    private int denomination;

    @Column
    private int quantity;

    @Enumerated(EnumType.STRING)
    private CashType type;
}
