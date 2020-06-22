package com.xyz.vendingmachine.machine;

import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.repository.ItemRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utils for test classes
 * @author amarenco
 */
public class TestUtils {
    /** Delta for data comparisons */
    public static final double DELTA = 0.00001;


    /**
     * @param itemRepository the item repository
     * @return a random, unassigned UUID
     */
    public static String createRandomItemUUID(ItemRepository itemRepository) {
        List<Item> items = itemRepository.findAll();
        AtomicReference<String> id = new AtomicReference<>();

        do {
            id.set(UUID.randomUUID().toString());
        }
        while (items.stream().anyMatch(item -> item.getId().toString().equals(id.get())));

        return id.get();
    }
}
