package com.hey.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class HeyGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeyGatewayApplication.class, args);
	}

}
