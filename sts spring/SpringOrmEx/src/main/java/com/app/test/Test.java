package com.app.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.scheduling.config.TaskNamespaceHandler;

import com.app.config.MyConfig;
import com.app.entity.Employee;

public class Test {

	public static void main(String[] args) {
		ApplicationContext ac=new AnnotationConfigApplicationContext(MyConfig.class);
		HibernateTemplate ht=(HibernateTemplate)ac.getBean("htObj");
		Employee e=ht.get(Employee.class,11);
		System.out.println(e);
	}
}
