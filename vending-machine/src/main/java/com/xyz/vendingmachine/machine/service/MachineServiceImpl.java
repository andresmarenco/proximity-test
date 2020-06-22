package com.xyz.vendingmachine.machine.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.xyz.vendingmachine.machine.MachineConfig;
import com.xyz.vendingmachine.machine.client.ServerClient;
import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.exception.ClosedMachineException;
import com.xyz.vendingmachine.machine.exception.LockedMachineException;
import com.xyz.vendingmachine.machine.model.OpenAttempt;
import com.xyz.vendingmachine.machine.model.StorageLocation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link MachineService}
 * @author amarenco
 */
@Service("machineService")
@Slf4j
public class MachineServiceImpl implements MachineService {

    @Autowired
    private StorageLocationService storageLocationService;

    @Autowired
    private MachineConfig machineConfig;

    @Autowired
    private OpenAttemptService openAttemptService;

    @Autowired
    private CashService cashService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @Autowired
    private ServerClient serverClient;

    @Getter
    private boolean open = false;


    @Override
    public void open(String code) throws IllegalArgumentException, LockedMachineException {
        if (open) {
            log.info("The machine is already open");
        } else {
            if (securityService.isServerRequest()) {
                log.info("Opening the machine...");
                this.open = true;
            } else {
                List<OpenAttempt> latestAttempts = openAttemptService.getLatestAttempts(machineConfig.getMaxOpenAttempts());

                if (!CollectionUtils.isEmpty(latestAttempts) && latestAttempts.size() == machineConfig.getMaxOpenAttempts() &&
                        latestAttempts.stream().allMatch(event -> !event.isSuccess() && LocalDate.from(event.getTimestamp()).isEqual(LocalDate.now(machineConfig.getDefaultClock())))) {

                    log.error("Machine locked!");
                    openAttemptService.logAttempt(false);

                    throw new LockedMachineException("The machine is locked due to several failed opening attempts");
                } else {
                    if (code.trim().equals(machineConfig.getSecurityCode())) {
                        log.info("Opening the machine...");
                        openAttemptService.logAttempt(true);
                        this.open = true;
                    } else {
                        log.error("Invalid security code");
                        openAttemptService.logAttempt(false);

                        throw new IllegalArgumentException("Invalid security code");
                    }
                }
            }
        }
    }


    @Override
    public void close() {
        if (!open) {
            log.info("The machine is already closed...");
        } else {
            log.info("Closing the machine...");
            this.open = false;
        }
    }


    @Override
    public boolean dispatchItem(String code, int quantity) {
        Optional<StorageLocation> location = storageLocationService.findByCode(code);
        if (location.isPresent()) {
            StorageLocation locationEntity = location.get();
            if (locationEntity.getQuantity() >= quantity) {
                log.info("Dispatching {} item(s) of the code {}", String.valueOf(quantity), code);
                storageLocationService.decrementInventory(locationEntity, quantity);

                return true;
            } else {
                log.error("No items left to dispatch of the code {}", code);
                return false;
            }
        } else {
            log.error("No item defined in the code {}", code);
            return false;
        }
    }


    @Override
    public BigDecimal getCurrentCashAmount() {
        BigDecimal total = cashService.countCash();
        return total != null ? total : BigDecimal.ZERO;
    }


    @Transactional
    @Override
    public List<CashDTO> retrieveCash() throws ClosedMachineException {
        if (open) {
            log.info("Retrieving all cash...");

            List<CashDTO> currentCash = cashService.getAll();
            cashService.clearCash();

            InstanceInfo instanceInfo = applicationInfoManager.getInfo();
            if (instanceInfo != null) {
                serverClient.deleteAlert(instanceInfo.getIPAddr(), instanceInfo.getPort());
            }

            return currentCash;
        } else {
            throw new ClosedMachineException("You must open the machine before retrieving the cash");
        }
    }


    @Override
    public void setCoins(int denomination, int quantity) throws ClosedMachineException {
        if (open) {
            cashService.setCoins(denomination, quantity);
        } else {
            throw new ClosedMachineException("You must open the machine before setting the coins");
        }
    }
}
