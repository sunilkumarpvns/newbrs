package com.brs.searchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages= {"com.brs.searchservice"})
public class BrsSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrsSearchServiceApplication.class, args);
	}
}
