package com.xyz.vendingmachine.machine.controller;

import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.dto.CashReturnDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.dto.ReadTransactionDTO;
import com.xyz.vendingmachine.machine.service.ItemService;
import com.xyz.vendingmachine.machine.service.SalesService;
import com.xyz.vendingmachine.machine.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for sales
 * @author amarenco
 */
@RestController
@RequestMapping("/sales")
@Validated
public class SalesController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ItemService itemService;

    @Autowired
    private SalesService salesService;

    @Autowired
    private TransactionService transactionService;


    /**
     * @return <code>200</code> with the list of transactions
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(transactionService.getAll().stream().map(ReadTransactionDTO::new).collect(Collectors.toList()));
    }


    /**
     * @return <code>200</code> with the total sales of the machine
     */
    @GetMapping(path = "/total", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getTotalSales() {
        return ResponseEntity.ok(Collections.singletonMap("total", salesService.getTotalSales()));
    }


    /**
     * @param day the day of the transactions (or null for all)
     * @return <code>200</code> with the daily sales of the machine
     */
    @GetMapping(path = "/dailyQuantity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getDailyTransactions(
            @RequestParam(required = false, name = "day") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {

        if (day == null) {
            return ResponseEntity.ok(salesService.getDailyTransactionsQuantity());
        } else {
            return ResponseEntity.ok(Collections.singletonMap("quantity", salesService.getDailyTransactionsQuantity(day)));
        }
    }


    /**
     * Sales an item by cash
     * @param code the code of the item
     * @param quantity the quantity to sell
     * @param cash the cash
     * @return <code>200</code> if the sale was successful; or
     * <code>400</code> if the sale failed
     */
    @PostMapping(path = "/{code}/cash", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> sellItem(
            @PathVariable("code") String code,
            @RequestParam(value = "quantity", defaultValue = "1") @Min(message = "The quantity must be at least 1", value = 1) int quantity,
            @RequestBody List<@Valid CashDTO> cash) {

        return ResponseEntity.ok(new CashReturnDTO(applicationContext, salesService.sellItem(code, quantity, cash)));
    }


    /**
     * Sales an item by credit card
     * @param code the code of the item
     * @param quantity the quantity to sell
     * @param creditCard the credit card information
     * @return <code>200</code> with a receipt if the sale was successful; or
     * <code>400</code> if the sale failed
     */
    @PostMapping(path = "/{code}/creditCard", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> sellItem(
            @PathVariable("code") String code,
            @RequestParam(value = "quantity", defaultValue = "1") @Min(message = "The quantity must be at least 1", value = 1) int quantity,
            @RequestBody @Valid CreditCardSaleDTO creditCard) {

        return ResponseEntity.ok(salesService.sellItem(code, quantity, creditCard));
    }
}
