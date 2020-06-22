package com.xyz.vendingmachine.machine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xyz.vendingmachine.machine.MachineConfig;
import com.xyz.vendingmachine.machine.TestUtils;
import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.model.CashTransaction;
import com.xyz.vendingmachine.machine.model.CashType;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.MachineType;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import com.xyz.vendingmachine.machine.repository.ItemRepository;
import com.xyz.vendingmachine.machine.repository.StorageLocationRepository;
import com.xyz.vendingmachine.machine.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link SalesController}
 * @author amarenco
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StorageLocationRepository storageLocationRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MachineConfig machineConfig;


    /**
     * Tests the endpoint defined in {@link SalesController#sellItem(String, int, CreditCardSaleDTO)}
     * @throws Exception
     */
    @Test
    public void sellItemCreditCardTest() throws Exception {
        Item itemA = itemRepository.findByNameIgnoreCase("coke").get();
        Item itemB = itemRepository.findByNameIgnoreCase("water").get();
        BigDecimal currentTotal = transactionRepository.getTotalSales();

        // Bad request
        mockMvc.perform(post("/sales/{code}/creditCard", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        // Some data is available
        storageLocationRepository.save(StorageLocation.builder()
                .code("A1")
                .item(itemA)
                .quantity(1)
                .build());

        storageLocationRepository.save(StorageLocation.builder()
                .code("A2")
                .item(itemB)
                .quantity(10)
                .build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        CreditCardSaleDTO creditCard = new CreditCardSaleDTO();
        creditCard.setName("Name");
        creditCard.setNumber("1234");
        creditCard.setExpirationDate(YearMonth.now());
        creditCard.setSecurityCode("123");


        // Unsupported machine
        mockMvc.perform(post("/sales/{code}/creditCard", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(creditCard)))
                .andExpect(status().isBadRequest());


        // Switch the machine
        MachineType currentMachineType = machineConfig.getMachineType();
        machineConfig.setMachineType(MachineType.XYZ2);

        mockMvc.perform(post("/sales/{code}/creditCard", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(creditCard)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$").exists());
        assertEquals(currentTotal.add(itemA.getCost()).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(0, storageLocationRepository.findById("A1").get().getQuantity());


        // No more items in A1
        mockMvc.perform(post("/sales/{code}/creditCard", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(creditCard)))
                .andExpect(status().isBadRequest());
        assertEquals(currentTotal.add(itemA.getCost()).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(0, storageLocationRepository.findById("A1").get().getQuantity());


        // Get item for A2
        StorageLocation storageLocationA2 = storageLocationRepository.findById("A2").get();
        mockMvc.perform(post("/sales/{code}/creditCard", "A2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(creditCard)))
                .andExpect(status().isOk());
        assertEquals(currentTotal
                .add(itemA.getCost())
                .add(itemB.getCost()).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(storageLocationA2.getQuantity() - 1, storageLocationRepository.findById("A2").get().getQuantity());


        // Not enough items
        mockMvc.perform(post("/sales/{code}/creditCard", "A2")
                .param("quantity", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(creditCard)))
                .andExpect(status().isBadRequest());


        // More than 1
        mockMvc.perform(post("/sales/{code}/creditCard", "A2")
                .queryParam("quantity", "5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(creditCard)))
                .andExpect(status().isOk());
        assertEquals(currentTotal
                .add(itemA.getCost())
                .add(itemB.getCost().multiply(BigDecimal.valueOf(6))).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(storageLocationA2.getQuantity() - 6, storageLocationRepository.findById("A2").get().getQuantity());


        // Clean up
        machineConfig.setMachineType(currentMachineType);
        storageLocationRepository.deleteById("A1");
        storageLocationRepository.deleteById("A2");
        transactionRepository.deleteAll();
    }


    /**
     * Tests the endpoint defined in {@link SalesController#sellItem(String, int, List)}
     * @throws Exception
     */
    @Test
    public void sellItemCashTest() throws Exception {
        Item itemA = itemRepository.findByNameIgnoreCase("coke").get();
        Item itemB = itemRepository.findByNameIgnoreCase("water").get();
        BigDecimal currentTotal = transactionRepository.getTotalSales();

        // Bad request
        ObjectMapper mapper = new ObjectMapper();
        List<CashDTO> cash = new ArrayList<>();
        cash.add(CashDTO.builder().denomination(5).type(CashType.COIN).quantity(0).build());

        mockMvc.perform(post("/sales/{code}/cash", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cash)))
                .andExpect(status().isBadRequest());

        // Some data is available
        storageLocationRepository.save(StorageLocation.builder()
                .code("A1")
                .item(itemA)
                .quantity(1)
                .build());

        storageLocationRepository.save(StorageLocation.builder()
                .code("A2")
                .item(itemB)
                .quantity(10)
                .build());


        // Invalid coins
        cash.clear();
        cash.add(CashDTO.builder().denomination(6).type(CashType.COIN).quantity(2).build());
        cash.add(CashDTO.builder().denomination(16).type(CashType.BILL).quantity(2).build());

        mockMvc.perform(post("/sales/{code}/cash", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cash)))
                .andExpect(status().isBadRequest());


        // Correct Sale
        cash.clear();
        cash.add(CashDTO.builder().denomination(5).type(CashType.COIN).quantity(2).build());
        cash.add(CashDTO.builder().denomination(10).type(CashType.COIN).quantity(1).build());
        cash.add(CashDTO.builder().denomination(1).type(CashType.BILL).quantity(1).build());

        mockMvc.perform(post("/sales/{code}/cash", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cash)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.total", is(0)));
        assertEquals(currentTotal.add(itemA.getCost()).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(0, storageLocationRepository.findById("A1").get().getQuantity());

        // No more items in A1
        mockMvc.perform(post("/sales/{code}/cash", "A1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cash)))
                .andExpect(status().isBadRequest());
        assertEquals(currentTotal.add(itemA.getCost()).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(0, storageLocationRepository.findById("A1").get().getQuantity());


        // Get item for A2
        cash.clear();
        cash.add(CashDTO.builder().denomination(1).type(CashType.BILL).quantity(1).build());

        StorageLocation storageLocationA2 = storageLocationRepository.findById("A2").get();
        mockMvc.perform(post("/sales/{code}/cash", "A2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cash)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.total", is(0.2)))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(currentTotal
                .add(itemA.getCost())
                .add(itemB.getCost()).doubleValue(), transactionRepository.getTotalSales().doubleValue(), TestUtils.DELTA);
        assertEquals(storageLocationA2.getQuantity() - 1, storageLocationRepository.findById("A2").get().getQuantity());


        // Not enough items
        mockMvc.perform(post("/sales/{code}/cash", "A2")
                .param("quantity", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cash)))
                .andExpect(status().isBadRequest());


        // Clean up
        storageLocationRepository.deleteById("A1");
        storageLocationRepository.deleteById("A2");
        transactionRepository.deleteAll();
    }


    /**
     * Tests the endpoint defined in {@link SalesController#getDailyTransactions(LocalDate)}
     * @throws Exception
     */
    @Test
    public void getDailyTransactionsTest() throws Exception {
        mockMvc.perform(get("/sales/dailyQuantity"))
                .andExpect(status().isOk());

        // Some data
        Item itemA = itemRepository.findByNameIgnoreCase("coke").get();
        CashTransaction transaction = new CashTransaction();
        transaction.setItem(itemA);
        transaction.setQuantity(2);
        transaction.setTotal(itemA.getCost().multiply(BigDecimal.valueOf(transaction.getQuantity())));
        transactionRepository.save(transaction);

        transaction = new CashTransaction();
        transaction.setItem(itemA);
        transaction.setQuantity(2);
        transaction.setTotal(itemA.getCost().multiply(BigDecimal.valueOf(transaction.getQuantity())));
        transactionRepository.save(transaction);

        mockMvc.perform(get("/sales/dailyQuantity"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());



        // Some data
        mockMvc.perform(get("/sales/dailyQuantity")
                .queryParam("day", "2019-01-01"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());


        // Clean up
        transactionRepository.deleteAll();
    }

}
