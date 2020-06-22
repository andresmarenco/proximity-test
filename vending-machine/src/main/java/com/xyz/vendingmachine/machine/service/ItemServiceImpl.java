package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link ItemService}
 * @author amarenco
 */
@Service("itemService")
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;


    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }


    @Override
    public Optional<Item> findById(String id) {
        return itemRepository.findById(UUID.fromString(id));
    }


    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }


    @Override
    public void delete(Item item) {
        itemRepository.delete(item);
    }
}
