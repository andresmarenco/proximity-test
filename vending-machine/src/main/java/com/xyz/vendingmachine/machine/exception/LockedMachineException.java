package com.xyz.vendingmachine.machine.exception;

/**
 * Exception thrown when the machine is locked
 * @author amarenco
 */
public class LockedMachineException extends RuntimeException {
    public LockedMachineException(String message) {
        super(message);
    }
}
