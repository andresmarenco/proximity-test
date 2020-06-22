package com.xyz.vendingmachine.machine.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.vendingmachine.machine.MachineConfig;
import com.xyz.vendingmachine.machine.TestUtils;
import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.CashType;
import com.xyz.vendingmachine.machine.repository.CashRepository;
import com.xyz.vendingmachine.machine.service.CashService;
import com.xyz.vendingmachine.machine.service.MachineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link MachineController}
 * @author amarenco
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MachineService machineService;

    @Autowired
    private MachineConfig machineConfig;

    @Autowired
    private CashService cashService;

    @Autowired
    private CashRepository cashRepository;


    /**
     * Tests the endpoint defined in {@link MachineController#openMachine(String)}
     * @throws Exception
     */
    @Test
    public void openMachineTest() throws Exception {
        // Correct code
        assertFalse(machineService.isOpen());
        mockMvc.perform(post("/machine/open")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", machineConfig.getSecurityCode()))
                .andExpect(status().isOk());
        assertTrue(machineService.isOpen());

        // Already open
        mockMvc.perform(post("/machine/open")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", machineConfig.getSecurityCode()))
                .andExpect(status().isOk());
        assertTrue(machineService.isOpen());

        machineService.close();
        assertFalse(machineService.isOpen());

        // Incorrect code
        mockMvc.perform(post("/machine/open")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", machineConfig.getSecurityCode() + "wrong"))
                .andExpect(status().isBadRequest());
        assertFalse(machineService.isOpen());

        // Correct code again
        mockMvc.perform(post("/machine/open")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", machineConfig.getSecurityCode()))
                .andExpect(status().isOk());
        assertTrue(machineService.isOpen());

        machineService.close();
        assertFalse(machineService.isOpen());

        // Lock the machine
        for (int i = 0; i < machineConfig.getMaxOpenAttempts(); i++) {
            mockMvc.perform(post("/machine/open")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("code", machineConfig.getSecurityCode() + "wrong"))
                    .andExpect(status().isBadRequest());
            assertFalse(machineService.isOpen());
        }

        // Correct code, but locked machine
        mockMvc.perform(post("/machine/open")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", machineConfig.getSecurityCode()))
                .andExpect(status().isBadRequest());
        assertFalse(machineService.isOpen());

        // Check tomorrow
        Clock currentClock = machineConfig.getDefaultClock();
        machineConfig.setDefaultClock(Clock.offset(currentClock, Duration.ofDays(1)));

        mockMvc.perform(post("/machine/open")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("code", machineConfig.getSecurityCode()))
                .andExpect(status().isOk());
        assertTrue(machineService.isOpen());

        // Clean up
        machineConfig.setDefaultClock(currentClock);
        machineService.close();
    }


    /**
     * Tests the endpoint defined in {@link MachineController#closeMachine()}
     * @throws Exception
     */
    @Test
    public void closeMachineTest() throws Exception {
        assertFalse(machineService.isOpen());

        mockMvc.perform(post("/machine/close")).andExpect(status().isOk());
        assertFalse(machineService.isOpen());

        machineService.open(machineConfig.getSecurityCode());
        assertTrue(machineService.isOpen());

        mockMvc.perform(post("/machine/close")).andExpect(status().isOk());
        assertFalse(machineService.isOpen());
    }


    /**
     * Tests the endpoint defined in {@link MachineController#getCurrentCashAmount()}
     * @throws Exception
     */
    @Test
    public void getCurrentCashAmountTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/machine/cash"))
                .andExpect(status().isOk())
                .andReturn();

        assertNotEquals(0d, new ObjectMapper().readTree(result.getResponse().getContentAsString()).get("total").asDouble(), TestUtils.DELTA);
    }


    /**
     * Tests the endpoint defined in {@link MachineController#retrieveCash()}
     * @throws Exception
     */
    @Test
    public void retrieveCashTest() throws Exception {
        double currentCash = cashService.countCash().doubleValue();
        assertNotEquals(0d, currentCash, TestUtils.DELTA);
        assertFalse(machineService.isOpen());

        // Closed machine
        mockMvc.perform(post("/machine/cash/retrieve")).andExpect(status().isBadRequest());

        // Retrieve the cash
        machineService.open(machineConfig.getSecurityCode());
        MvcResult result = mockMvc.perform(post("/machine/cash/retrieve")).andExpect(status().isOk()).andReturn();
        assertEquals(0d, cashService.countCash().doubleValue(), TestUtils.DELTA);

        // Clean up
        List<CashDTO> cashList = new ObjectMapper().readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<List<CashDTO>>() {});

        cashList.forEach(cash -> {
            if (cash.getType() == CashType.COIN) {
                cashService.setCoins(cash.getDenomination(), cash.getQuantity());
            } else {
                cashRepository.save(Cash.builder()
                        .denomination(cash.getDenomination())
                        .quantity(cash.getQuantity())
                        .type(cash.getType())
                        .build());
            }
        });

        assertEquals(currentCash, cashService.countCash().doubleValue(), TestUtils.DELTA);
    }


    /**
     * Tests the endpoint defined in {@link MachineController#setCoins(Integer, Integer)}
     * @throws Exception
     */
    @Test
    public void setCoinsTest() throws Exception {
        double currentCash = cashService.countCash().doubleValue();
        assertFalse(machineService.isOpen());

        // Closed machine
        mockMvc.perform(post("/machine/cash/coins")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("denomination", "5")
                .param("quantity", "1"))
                .andExpect(status().isBadRequest());

        // Invalid data
        machineService.open(machineConfig.getSecurityCode());
        mockMvc.perform(post("/machine/cash/coins")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("denomination", "6")
                .param("quantity", "-1"))
                .andExpect(status().isBadRequest());

        // Set some data
        Cash cash = cashRepository.findById(5).get();

        mockMvc.perform(post("/machine/cash/coins")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("denomination", "5")
                .param("quantity", String.valueOf(cash.getQuantity() + 1)))
                .andExpect(status().isOk());

        assertEquals(currentCash + 0.05, cashService.countCash().doubleValue(), TestUtils.DELTA);


        // Clean up
        mockMvc.perform(post("/machine/cash/coins")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("denomination", "5")
                .param("quantity", String.valueOf(cash.getQuantity())))
                .andExpect(status().isOk());

        assertEquals(currentCash, cashService.countCash().doubleValue(), TestUtils.DELTA);

        // Clean up
        machineService.close();
    }
}
