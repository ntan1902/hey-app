package com.hey.service.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HeyRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeyRegistryApplication.class, args);
	}

}
