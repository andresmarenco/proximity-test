package com.xyz.vendingmachine.machine.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract JPA Entity for Sale Transactions
 * @author amarenco
 */
@Entity
@Table(name = "transaction")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
public abstract class Transaction {
    @Id
    @GeneratedValue
    protected UUID id;

    @Column(name = "sale_timestamp")
    @CreationTimestamp
    protected LocalDateTime timestamp;

    @ManyToOne
    protected Item item;

    @Column
    protected int quantity;

    @Column
    protected BigDecimal total;
}
