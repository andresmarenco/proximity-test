package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import com.xyz.vendingmachine.machine.repository.StorageLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link StorageLocationService}
 * @author amarenco
 */
@Service("storageLocationService")
public class StorageLocationServiceImpl implements StorageLocationService {

    @Autowired
    private StorageLocationRepository storageLocationRepository;


    @Override
    public Optional<StorageLocation> findByCode(String code) {
        return storageLocationRepository.findById(code);
    }

    @Override
    public int decrementInventory(StorageLocation storageLocation) {
        return this.decrementInventory(storageLocation, 1);
    }


    @Override
    public int decrementInventory(StorageLocation storageLocation, int quantity) {
        storageLocation.setQuantity(storageLocation.getQuantity() - quantity);
        storageLocationRepository.save(storageLocation);

        return storageLocation.getQuantity();
    }


    @Override
    public List<StorageLocation> listItems() {
        return storageLocationRepository.findAll(Sort.by("code"));
    }


    @Override
    public void defineItem(String code, Item item, int quantity) {
        StorageLocation location = storageLocationRepository.findById(code).orElseGet(() -> new StorageLocation());
        location.setCode(code);
        location.setItem(item);
        location.setQuantity(quantity);

        storageLocationRepository.save(location);
    }


    @Override
    public void clearLocation(String code) {
        storageLocationRepository.deleteById(code);
    }
}
