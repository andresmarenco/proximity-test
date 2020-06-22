package com.xyz.vendingmachine.server.service;

import com.xyz.vendingmachine.server.model.Alert;
import com.xyz.vendingmachine.server.model.VendingMachine;

/**
 * Service definition for {@link Alert} entities
 * @author amarenco
 */
public interface AlertService {
    /**
     * @param machine the machine that creates the alert
     */
    void createAlert(VendingMachine machine);

    /**
     * @param machine the machine that removes the alert
     */
    void removeAlert(VendingMachine machine);

    /**
     * @param machine the machine to check
     * @return <code>true</code> if the machine as an active alert
     */
    boolean hasAlert(VendingMachine machine);
}
