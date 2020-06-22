package com.xyz.vendingmachine.machine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.vendingmachine.machine.TestUtils;
import com.xyz.vendingmachine.machine.dto.SaveItemDTO;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ItemController}
 * @author amarenco
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;


    /**
     * Tests the endpoint defined in {@link ItemController#getAll()}
     * @throws Exception
     */
    @Test
    public void getAllTest() throws Exception {
        mockMvc.perform(get("/item"))
                .andExpect(status().isOk());

        // Check the result when no data is available
        List<Item> items = itemRepository.findAll();
        itemRepository.deleteAll();

        mockMvc.perform(get("/item"))
                .andExpect(status().isNoContent());

        itemRepository.saveAll(items);
    }


    /**
     * Tests the endpoint defined in {@link ItemController#findById(String)}
     * @throws Exception
     */
    @Test
    public void findByIdTest() throws Exception {
        // Invalid ID
        mockMvc.perform(get("/item/{id}", "a-b-c"))
                .andExpect(status().isBadRequest());

        // Not found
        String id = TestUtils.createRandomItemUUID(itemRepository);
        mockMvc.perform(get("/item/{id}", id))
                .andExpect(status().isNotFound());

        // Found
        Item coke = itemRepository.findByNameIgnoreCase("coke").get();
        mockMvc.perform(get("/item/{id}", coke.getId()))
                .andExpect(status().isOk());
    }


    /**
     * Tests the endpoint defined in {@link ItemController#create(SaveItemDTO, UriComponentsBuilder)}
     * @throws Exception
     */
    @Test
    public void createTest() throws Exception {
        // No data
        mockMvc.perform(post("/item")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Invalid data
        ObjectMapper mapper = new ObjectMapper();

        SaveItemDTO item = new SaveItemDTO();
        item.setName("");
        item.setCost(BigDecimal.valueOf(-1));

        mockMvc.perform(post("/item")
                .content(mapper.writeValueAsString(item))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Correct data
        item = new SaveItemDTO();
        item.setName("Ginger Ale");
        item.setCost(BigDecimal.valueOf(1.5d));

        MvcResult result = mockMvc.perform(post("/item")
                .content(mapper.writeValueAsString(item))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Clean up
        Optional<Item> storedItem = itemRepository.findById(UUID.fromString(mapper.readTree(result.getResponse().getContentAsString()).get("id").asText()));
        assertTrue(storedItem.isPresent());
        itemRepository.delete(storedItem.get());
    }


    /**
     * Tests the endpoint defined in {@link ItemController#update(String, SaveItemDTO)}
     * @throws Exception
     */
    @Test
    public void updateTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Item coke = itemRepository.findByNameIgnoreCase("coke").get();

        SaveItemDTO item = new SaveItemDTO();
        item.setName("Ginger Ale");
        item.setCost(BigDecimal.valueOf(1.5d));

        // Not found
        String id = TestUtils.createRandomItemUUID(itemRepository);
        mockMvc.perform(put("/item/{id}", id)
                .content(mapper.writeValueAsString(item))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // No data
        mockMvc.perform(put("/item/{id}", coke.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Invalid data
        SaveItemDTO invalidItem = new SaveItemDTO();
        invalidItem.setName("");
        invalidItem.setCost(BigDecimal.valueOf(-1));

        mockMvc.perform(put("/item/{id}", coke.getId().toString())
                .content(mapper.writeValueAsString(invalidItem))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Correct data
        mockMvc.perform(put("/item/{id}", coke.getId().toString())
                .content(mapper.writeValueAsString(item))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Item updatedItem = itemRepository.findById(coke.getId()).get();
        assertEquals(item.getName(), updatedItem.getName());
        assertEquals(item.getCost().doubleValue(), updatedItem.getCost().doubleValue(), TestUtils.DELTA);

        // Clean up
        updatedItem.setName(coke.getName());
        updatedItem.setCost(coke.getCost());
        itemRepository.save(updatedItem);
    }


    /**
     * Tests the endpoint defined in {@link ItemController#delete(String)}
     * @throws Exception
     */
    @Test
    public void deleteTest() throws Exception {
        // Not found
        String id = TestUtils.createRandomItemUUID(itemRepository);
        mockMvc.perform(delete("/item/{id}", id))
                .andExpect(status().isNotFound());

        // Correct data
        Item coke = itemRepository.findByNameIgnoreCase("coke").get();
        mockMvc.perform(delete("/item/{id}", coke.getId().toString()))
                .andExpect(status().isOk());

        assertFalse(itemRepository.existsById(coke.getId()));

        // Clean up
        itemRepository.save(coke);
    }
}
