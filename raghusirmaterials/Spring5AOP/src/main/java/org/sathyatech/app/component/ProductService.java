package org.sathyatech.app.component;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

	public void getProductInfo() {
		System.out.println("Product Code is :554SYH");
		//throw new RuntimeException("Test Exception"); //-To see AfterThrowing
	}
}
