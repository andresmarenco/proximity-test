package com.xyz.vendingmachine.server.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.xyz.vendingmachine.server.dto.VendingMachineStatusDTO;
import com.xyz.vendingmachine.server.model.VendingMachine;
import com.xyz.vendingmachine.server.repository.VendingMachineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link VendingMachineService}
 * @author amarenco
 */
@Service("vendingMachineService")
@Slf4j
public class VendingMachineServiceImpl implements VendingMachineService {
    /** The name of the vending machine application */
    private static final String VENDING_MACHINE_APPLICATION = "vending-machine";
    /** The path to open a remote machine */
    private static final String MACHINE_OPEN_PATH = "/machine/open";

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private VendingMachineRepository vendingMachineRepository;

    @Autowired
    private AlertService alertService;


    @Override
    public List<VendingMachineStatusDTO> getAll() {
        List<VendingMachineStatusDTO> result = new ArrayList<>();
        vendingMachineRepository.findAll().forEach(machine -> result.add(new VendingMachineStatusDTO(machine)));

        Application application = eurekaClient.getApplication(VENDING_MACHINE_APPLICATION);
        if (application != null) {
            application.getInstances().forEach(instance -> {
                Optional<VendingMachineStatusDTO> dto = result.stream()
                        .filter(machine -> machine.getRemoteAddress().equals(instance.getIPAddr()))
                        .filter(machine -> machine.getPort() == instance.getPort())
                        .findFirst();

                if (!dto.isPresent()) {
                    log.info("Adding vending machine on IP: {}, port {}", instance.getHostName(), String.valueOf(instance.getPort()));

                    // TODO: Instead of assuming that the machine is new, retrieve its current status
                    dto = Optional.of(new VendingMachineStatusDTO(vendingMachineRepository.save(new VendingMachine(instance))));
                    result.add(dto.get());
                }

                dto.get().setActive(instance.getStatus() == InstanceInfo.InstanceStatus.UP);
            });
        }

        result.forEach(machine -> {
            machine.setCollect(alertService.hasAlert(machine.getMachine()));
        });

        return result;
    }


    @Override
    public Optional<VendingMachine> findById(UUID id) {
        return vendingMachineRepository.findById(id);
    }


    @Override
    public void updateBalance(VendingMachine machine, BigDecimal balance) {
        machine.setBalance(balance);
        vendingMachineRepository.save(machine);
    }


    @Override
    public VendingMachine registerMachine(String ip, int port) {
        log.info("Adding vending machine on IP: {}, port {}", ip, String.valueOf(port));

        VendingMachine machine = new VendingMachine();
        machine.setRemoteAddress(ip);
        machine.setPort(port);
        machine.setBalance(BigDecimal.ZERO);

        return vendingMachineRepository.save(machine);
    }


    @Override
    public Optional<VendingMachine> findByRemoteAddress(String ip, int port) {
        return vendingMachineRepository.findByRemoteAddressAndPort(ip, port);
    }


    @Override
    public void openMachine(VendingMachine machine) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("code", "NO_CODE");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(machine.getRemoteAddress())
                .port(machine.getPort())
                .path(MACHINE_OPEN_PATH)
                .build().toUri();

        restTemplate.postForEntity(uri, request, String.class);
    }
}
