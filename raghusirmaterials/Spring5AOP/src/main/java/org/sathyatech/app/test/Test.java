package org.sathyatech.app.test;

import org.sathyatech.app.component.ProductService;
import org.sathyatech.app.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac=new AnnotationConfigApplicationContext(AppConfig.class);
		
		ProductService ps=ac.getBean(ProductService.class);
		ps.getProductInfo();
		
		ac.close();
	}
}
