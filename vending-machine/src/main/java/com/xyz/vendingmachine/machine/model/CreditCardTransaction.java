package com.xyz.vendingmachine.machine.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * JPA Entity for a Credit/Debit Card Transaction
 * @author amarenco
 */
@Entity
@DiscriminatorValue("CREDIT")
public class CreditCardTransaction extends Transaction {
}
