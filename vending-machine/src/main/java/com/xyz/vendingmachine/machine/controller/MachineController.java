package com.xyz.vendingmachine.machine.controller;

import com.xyz.vendingmachine.machine.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.Collections;

/**
 * REST Controller for the status of the machine
 * @author amarenco
 */
@RestController
@RequestMapping("/machine")
@Validated
public class MachineController {

    @Autowired
    private MachineService machineService;


    /**
     * Opens the machine
     * @param code the security code
     * @return <code>200</code> if the machine was successfully opened
     */
    @PostMapping(path = "/open", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> openMachine(@RequestParam("code") String code) {
        machineService.open(code);
        return ResponseEntity.ok().build();
    }


    /**
     * Closes the machine
     * @return <code>200</code> if the machine was successfully closed
     */
    @PostMapping(path = "/close")
    public ResponseEntity<?> closeMachine() {
        machineService.close();
        return ResponseEntity.ok().build();
    }


    /**
     * Gets the current cash amount in the machine
     * @return <code>200</code> with the current amount
     */
    @GetMapping(path = "/cash", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getCurrentCashAmount() {
        return ResponseEntity.ok(Collections.singletonMap("total", machineService.getCurrentCashAmount()));
    }


    /**
     * Retrieves the current cash from the machine. Note that the machine must be open
     * @return <code>200</code> with the retrieved cash; or
     * <code>400</code> if the machine was closed
     */
    @PostMapping(path = "/cash/retrieve", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> retrieveCash() {
        return ResponseEntity.ok(machineService.retrieveCash());
    }


    /**
     * Sets the quantity of coins of the given denomination. Note that the machine must be open
     * @param denomination the denomination of the coin
     * @param quantity the quantity of coins
     * @return <code>200</code> if the set was successful; or
     * <code>400</code> if the data is incorrect or the machine was closed
     */
    @PostMapping(path = "/cash/coins", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> setCoins(
            @RequestParam("denomination") @Min(message = "The denomination must me greater than 0", value = 1) Integer denomination,
            @RequestParam("quantity") @Min(message = "The quantity must me greater than 0", value = 0) Integer quantity) {

        machineService.setCoins(denomination, quantity);
        return ResponseEntity.ok().build();
    }
}
