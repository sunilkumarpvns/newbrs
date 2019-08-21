package com.app.service;

import org.springframework.stereotype.Service;

@Service
public class Product {

	public void getProductInfo() {
		System.out.println("Product Code is :554SYH");
		//throw new RuntimeException("Test Exception"); //-To see AfterThrowing
	}
}
