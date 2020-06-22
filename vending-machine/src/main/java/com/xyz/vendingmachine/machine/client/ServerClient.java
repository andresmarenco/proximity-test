package com.xyz.vendingmachine.machine.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Feign client for the server application
 * @author amarenco
 */
@FeignClient("server")
@RequestMapping("/machine")
public interface ServerClient {
    @PostMapping(path = "/alert", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void createAlert(@RequestParam("remoteAddress") String remoteAddress, @RequestParam("port") int port);

    @DeleteMapping(path = "/alert", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void deleteAlert(@RequestParam("remoteAddress") String remoteAddress, @RequestParam("port") int port);

    @PutMapping(path = "/balance", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void updateBalance(
            @RequestParam("remoteAddress") String remoteAddress,
            @RequestParam("port") int port,
            @RequestParam("balance") BigDecimal balance);
}
