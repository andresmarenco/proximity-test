package com.xyz.vendingmachine.machine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * JPA Entity for the storage location on the machine
 */
@Entity
@Table(name = "storage_location")
@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocation {
    @Id
    private String code;

    @ManyToOne
    private Item item;

    @Column
    private int quantity;
}
