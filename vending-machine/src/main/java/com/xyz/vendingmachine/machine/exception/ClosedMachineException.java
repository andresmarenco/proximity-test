package com.xyz.vendingmachine.machine.exception;

/**
 * Exception thrown when the machine is closed
 * @author amarenco
 */
public class ClosedMachineException extends RuntimeException {
    public ClosedMachineException(String message) {
        super(message);
    }
}
