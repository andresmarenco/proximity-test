package com.xyz.vendingmachine.machine.repository;

import com.xyz.vendingmachine.machine.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for {@link Item} entities
 * @author amarenco
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    /**
     * @param name the name of the item
     * @return the item or {@link Optional#empty()}
     */
    Optional<Item> findByNameIgnoreCase(String name);
}
