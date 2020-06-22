package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.model.OpenAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Service definition for OpenAttempt entities
 * @author amarenco
 */
public interface OpenAttemptService {
    /**
     * @param success <code>true</code> if the attempt was successful
     */
    void logAttempt(boolean success);

    /**
     * @param total the max number of events to retrieve
     * @return the latest open attempt events
     */
    List<OpenAttempt> getLatestAttempts(int total);
}
