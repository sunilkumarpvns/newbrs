package com.brs.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
@EnableEurekaServer
@SpringBootApplication
public class BrsEurekaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrsEurekaServiceApplication.class, args);
	}
}
