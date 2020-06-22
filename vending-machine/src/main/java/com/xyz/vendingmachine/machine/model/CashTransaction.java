package com.xyz.vendingmachine.machine.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * JPA Entity for a Cash Transaction
 * @author amarenco
 */
@Entity
@DiscriminatorValue("CASH")
public class CashTransaction extends Transaction {
}
