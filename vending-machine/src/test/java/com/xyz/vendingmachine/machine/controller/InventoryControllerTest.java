package com.xyz.vendingmachine.machine.controller;

import com.xyz.vendingmachine.machine.TestUtils;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import com.xyz.vendingmachine.machine.repository.ItemRepository;
import com.xyz.vendingmachine.machine.repository.StorageLocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ItemController}
 * @author amarenco
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StorageLocationRepository storageLocationRepository;


    /**
     * Tests the endpoint defined in {@link InventoryController#getAll()}
     * @throws Exception
     */
    @Test
    public void getAllTest() throws Exception {
        // No data
        mockMvc.perform(get("/inventory"))
                .andExpect(status().isNoContent());

        // Some data is available
        Item item = itemRepository.findByNameIgnoreCase("coke").get();
        storageLocationRepository.save(StorageLocation.builder()
                .code("A1")
                .item(item)
                .quantity(1)
                .build());

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk());

        // Clean up
        storageLocationRepository.deleteById("A1");
    }


    /**
     * Tests the endpoint defined in {@link InventoryController#getOne(String)}
     * @throws Exception
     */
    @Test
    public void getOneTest() throws Exception {
        // No data
        mockMvc.perform(get("/inventory/{code}", "A1"))
                .andExpect(status().isNotFound());

        // Some data is available
        Item item = itemRepository.findByNameIgnoreCase("coke").get();
        storageLocationRepository.save(StorageLocation.builder()
                .code("A1")
                .item(item)
                .quantity(1)
                .build());

        mockMvc.perform(get("/inventory/{code}", "A1"))
                .andExpect(status().isOk());

        // Clean up
        storageLocationRepository.deleteById("A1");
    }


    /**
     * Tests the endpoint defined in {@link InventoryController#defineItem(String, String, Integer)}
     * @throws Exception
     */
    @Test
    public void defineItemTest() throws Exception {
        String itemId = TestUtils.createRandomItemUUID(itemRepository);
        final String codeA = "A1";
        Item itemA = itemRepository.findByNameIgnoreCase("coke").get();
        Item itemB = itemRepository.findByNameIgnoreCase("sprite").get();

        // Missing params
        mockMvc.perform(post("/inventory/{code}", codeA)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("itemId", TestUtils.createRandomItemUUID(itemRepository)))
                .andExpect(status().isBadRequest());

        // Not found
        mockMvc.perform(post("/inventory/{code}", codeA)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("quantity", "1")
                .param("itemId", TestUtils.createRandomItemUUID(itemRepository)))
                .andExpect(status().isNotFound());

        // Assigned
        mockMvc.perform(post("/inventory/{code}", codeA)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("quantity", "1")
                .param("itemId", itemA.getId().toString()))
                .andExpect(status().isOk());

        Optional<StorageLocation> location = storageLocationRepository.findById(codeA);
        assertTrue(location.isPresent());
        assertEquals(itemA.getId(), location.get().getItem().getId());
        assertEquals(1, location.get().getQuantity());

        // Reassigned
        mockMvc.perform(post("/inventory/{code}", codeA)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("quantity", "5")
                .param("itemId", itemB.getId().toString()))
                .andExpect(status().isOk());

        location = storageLocationRepository.findById(codeA);
        assertTrue(location.isPresent());
        assertEquals(itemB.getId(), location.get().getItem().getId());
        assertEquals(5, location.get().getQuantity());


        // Clean up
        storageLocationRepository.delete(location.get());
    }


    /**
     * Tests the endpoint defined in {@link InventoryController#clearLocation(String)}
     * @throws Exception
     */
    @Test
    public void clearLocationTest() throws Exception {
        final String codeA = "A1";
        Item itemA = itemRepository.findByNameIgnoreCase("coke").get();
        storageLocationRepository.save(
                StorageLocation.builder()
                        .code(codeA)
                        .item(itemA)
                        .quantity(5)
                        .build());

        assertTrue(storageLocationRepository.existsById(codeA));

        mockMvc.perform(delete("/inventory/{code}", codeA))
                .andExpect(status().isOk());

        assertFalse(storageLocationRepository.existsById(codeA));
    }
}
