package com.brs.searchservice.cfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppCfg {
    @Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
