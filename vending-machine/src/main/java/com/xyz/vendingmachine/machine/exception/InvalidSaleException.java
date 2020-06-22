package com.xyz.vendingmachine.machine.exception;

/**
 * Exception thrown when a sale is incorrect
 * @author amarenco
 */
public class InvalidSaleException extends RuntimeException {
    public InvalidSaleException(String message) {
        super(message);
    }
}
