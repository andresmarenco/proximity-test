package com.xyz.vendingmachine.server.controller;

import com.xyz.vendingmachine.server.model.VendingMachine;
import com.xyz.vendingmachine.server.service.AlertService;
import com.xyz.vendingmachine.server.service.VendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST Controller for the registered vending machines
 * @author amarenco
 */
@RestController
@RequestMapping("/machine")
@Validated
public class VendingMachineController {

    @Autowired
    private VendingMachineService vendingMachineService;

    @Autowired
    private AlertService alertService;


    /**
     * @return <code>200</code> with a list of all registered vending machines and their status
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(vendingMachineService.getAll());
    }


    /**
     * @param id the id of the machine to open
     * @return <code>200</code> if the machine was successfully opened; or
     * <code>404</code> if the machine was not found; or
     * <code>400</code> if an error happens
     */
    @PostMapping(path = "/{id}/open")
    public ResponseEntity<?> openMachine(@PathVariable("id") String id) {
        VendingMachine machine = vendingMachineService.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The machine was not found"));

        vendingMachineService.openMachine(machine);
        return ResponseEntity.ok().build();
    }


    /**
     * @param remoteAddress the remoteAddress of the machine to open
     * @param port the port of the machine to open
     * @return <code>200</code> if the alert was created; or
     * <code>400</code> if an error happens
     */
    @PostMapping(path = "/alert", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createAlert(@RequestParam("remoteAddress") String remoteAddress, @RequestParam("port") int port) {
        VendingMachine machine = vendingMachineService.findByRemoteAddress(remoteAddress, port)
                .orElseGet(() -> vendingMachineService.registerMachine(remoteAddress, port));

        alertService.createAlert(machine);
        return ResponseEntity.ok().build();
    }


    /**
     * @param remoteAddress the remoteAddress of the machine to open
     * @param port the port of the machine to open
     * @return <code>200</code> if the alert was created; or
     * <code>400</code> if an error happens
     */
    @DeleteMapping(path = "/alert", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> removeAlert(@RequestParam("remoteAddress") String remoteAddress, @RequestParam("port") int port) {
        VendingMachine machine = vendingMachineService.findByRemoteAddress(remoteAddress, port)
                .orElseGet(() -> vendingMachineService.registerMachine(remoteAddress, port));

        alertService.removeAlert(machine);
        return ResponseEntity.ok().build();
    }


    /**
     * @param remoteAddress the remoteAddress of the machine to open
     * @param port the port of the machine to open
     * @param balance the new balance
     * @return <code>200</code> if the balance was updated; or
     * <code>400</code> if an error happens
     */
    @PutMapping(path = "/balance", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> updateBalance(
            @RequestParam("remoteAddress") String remoteAddress,
            @RequestParam("port") int port,
            @RequestParam("balance") @DecimalMin(message = "The balance must be at least 0", value = "0") BigDecimal balance) {

        VendingMachine machine = vendingMachineService.findByRemoteAddress(remoteAddress, port)
                .orElseGet(() -> vendingMachineService.registerMachine(remoteAddress, port));

        vendingMachineService.updateBalance(machine, balance);
        return ResponseEntity.ok().build();
    }
}
