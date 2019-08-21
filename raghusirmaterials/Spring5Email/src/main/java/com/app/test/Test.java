package com.app.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.app.config.AppConfig;
import com.app.util.AppMailSender;

public class Test {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext c=new AnnotationConfigApplicationContext(AppConfig.class);
		AppMailSender mailsender=(AppMailSender)c.getBean("appMailSender");
		
		FileSystemResource file=new FileSystemResource("C:\\Users\\Public\\Pictures\\Sample Pictures\\Jellyfish.jpg");
		boolean flag=mailsender.sendEmail("sunilkumar.datapoint@gmail.com", "Hello Test", "welcome to Email App", file);
		if(flag)
			 System.out.println("done");
		else
			System.out.println("fail");
		
		
		
		
	}
}
