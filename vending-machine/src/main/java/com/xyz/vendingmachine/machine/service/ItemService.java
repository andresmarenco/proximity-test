package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Service definition for {@link Item} entities
 * @author amarenco
 */
public interface ItemService {
    /**
     * @return a list with all the available items
     */
    List<Item> getAll();

    /**
     * @param id the id of the item
     * @return the item or {@link Optional#empty()}
     */
    Optional<Item> findById(String id);

    /**
     * Creates/updates the given item
     * @param item the item to save
     * @return the saved item
     */
    Item save(Item item);

    /**
     * Deletes the given item
     * @param item the item to delete
     */
    void delete(Item item);
}
