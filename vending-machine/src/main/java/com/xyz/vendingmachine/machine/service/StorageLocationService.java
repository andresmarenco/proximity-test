package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import com.xyz.vendingmachine.machine.repository.StorageLocationRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service definition for the storage location
 * @author amarenco
 */
public interface StorageLocationService {
    /**
     * @param code the code of the location
     * @return the details of the storage location or {@link Optional#empty()}
     */
    Optional<StorageLocation> findByCode(String code);

    /**
     * Decrements the quantity of the item in the given code
     * @param storageLocation the storage location
     * @return the new quantity
     */
    int decrementInventory(StorageLocation storageLocation);

    /**
     * Decrements the quantity of the item in the given code
     * @param storageLocation the storage location
     * @param quantity the quantity to decrement
     * @return the new quantity
     */
    int decrementInventory(StorageLocation storageLocation, int quantity);

    /**
     * @return a list of all storage location and their corresponding item
     */
    List<StorageLocation> listItems();

    /**
     * Defines the item available in the location
     * @param code the code of the location
     * @param item the item to set
     * @param quantity the quantity of the item
     */
    void defineItem(String code, Item item, int quantity);

    /**
     * Clears the item defined in the code
     * @param code the code of the location
     */
    void clearLocation(String code);
}
