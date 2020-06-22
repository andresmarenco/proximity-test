package com.xyz.vendingmachine.machine.service;

/**
 * Service definition for security
 * @author amarenco
 */
public interface SecurityService {
    /**
     * @return <code>true</code> if the request is from the server
     */
    boolean isServerRequest();
}
