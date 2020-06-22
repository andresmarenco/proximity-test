package com.xyz.vendingmachine.server.service;

import com.xyz.vendingmachine.server.model.Alert;
import com.xyz.vendingmachine.server.model.VendingMachine;
import com.xyz.vendingmachine.server.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default implementation of {@link AlertService}
 * @author amarenco
 */
@Service("alertService")
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;


    @Override
    public void createAlert(VendingMachine machine) {
        alertRepository.save(Alert.builder()
                .machine(machine)
                .solved(false)
                .build());
    }


    @Override
    public void removeAlert(VendingMachine machine) {
        if (hasAlert(machine)) {
            alertRepository.save(Alert.builder()
                    .machine(machine)
                    .solved(true)
                    .build());
        }
    }


    @Override
    public boolean hasAlert(VendingMachine machine) {
        Optional<Alert> alert = alertRepository.findFirstByMachineOrderByTimestampDesc(machine);
        return alert.isPresent() && !alert.get().isSolved();
    }
}
