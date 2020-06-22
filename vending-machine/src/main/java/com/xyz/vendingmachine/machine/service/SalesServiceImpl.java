package com.xyz.vendingmachine.machine.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.xyz.vendingmachine.machine.MachineConfig;
import com.xyz.vendingmachine.machine.client.ServerClient;
import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardReceiptDTO;
import com.xyz.vendingmachine.machine.dto.CreditCardSaleDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.Item;
import com.xyz.vendingmachine.machine.model.MachineType;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import com.xyz.vendingmachine.machine.repository.DailyTransactionsQuantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Default implementation of {@link SalesService}
 * @author amarenco
 */
@Service("salesService")
public class SalesServiceImpl implements SalesService {

    @Autowired
    private StorageLocationService storageLocationService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private MachineConfig machineConfig;

    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @Autowired
    private ServerClient serverClient;


    @Override
    public BigDecimal getTotalSales() {
        return transactionService.getTotalSales();
    }


    @Override
    public long getDailyTransactionsQuantity(LocalDate day) {
        return transactionService.getDailyTransactionsQuantity(day);
    }


    @Override
    public List<DailyTransactionsQuantity> getDailyTransactionsQuantity() {
        return transactionService.getDailyTransactionsQuantity();
    }


    @Override
    public Map<Cash, AtomicInteger> sellItem(String code, int quantity, List<CashDTO> cash) throws InvalidSaleException {
        return sellItem(code, quantity, item -> transactionService.process(item, quantity, cash));
    }


    @Transactional
    @Override
    public CreditCardReceiptDTO sellItem(String code, int quantity, CreditCardSaleDTO creditCard) throws InvalidSaleException {
        CreditCardReceiptDTO result = null;

        if (machineConfig.getMachineType() == MachineType.XYZ2) {
            result = sellItem(code, quantity, item -> transactionService.process(item, quantity, creditCard));
        } else {
            throw new InvalidSaleException("This machine doesn't support credit card transactions");
        }

        return result;
    }


    /**
     * @param code the code of the item
     * @param quantity the quantity of the item
     * @param function the function to process the transaction
     * @param <R> the result type of the transaction processing
     * @return the result of the transaction function
     * @throws InvalidSaleException if the transaction could not be processed
     */
    private <R> R sellItem(String code, int quantity, Function<Item, R> function) {
        R result = null;

        Optional<StorageLocation> location = storageLocationService.findByCode(code);
        if (location.isPresent()) {
            StorageLocation locationEntity = location.get();
            if (locationEntity.getItem() != null && locationEntity.getQuantity() >= quantity) {
                result = function.apply(locationEntity.getItem());
                machineService.dispatchItem(code, quantity);

                InstanceInfo instanceInfo = applicationInfoManager.getInfo();
                if (instanceInfo != null) {
                    serverClient.updateBalance(
                            instanceInfo.getIPAddr(),
                            instanceInfo.getPort(),
                            transactionService.getTotalSales());

                    if (machineService.getCurrentCashAmount().compareTo(machineConfig.getAlertThreshold()) >= 0) {
                        serverClient.createAlert(instanceInfo.getIPAddr(), instanceInfo.getPort());
                    }
                }
            } else {
                throw new InvalidSaleException("There are no items in the code");
            }
        } else {
            throw new InvalidSaleException("There are no items in the code");
        }

        return result;
    }
}
