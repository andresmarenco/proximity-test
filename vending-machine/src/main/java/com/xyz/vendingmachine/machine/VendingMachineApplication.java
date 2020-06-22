package com.xyz.vendingmachine.machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VendingMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendingMachineApplication.class, args);
	}

}
