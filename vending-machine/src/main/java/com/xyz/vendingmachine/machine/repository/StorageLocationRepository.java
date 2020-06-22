package com.xyz.vendingmachine.machine.repository;

import com.xyz.vendingmachine.machine.model.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for {@link StorageLocation} entities
 * @author amarenco
 */
@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, String> {
}
