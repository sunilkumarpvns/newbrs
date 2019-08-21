package com.sathya.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sathya.entity.Employee;
import com.sathya.util.HibernateUtil;

public class Test {
	public static void main(String args[ ])
	{
		SessionFactory  factory=HibernateUtil.getSessionFactory();
		Session  session1 = factory.openSession();
		Session  session2 = factory.openSession();
		Session  session3 = factory.openSession();
		
		Employee  e1=(Employee)session1.get(Employee.class, 7788);
		session1.clear();
		Employee  e2=(Employee)session1.get(Employee.class, 7788);
		try {
			Thread.sleep(14000);
		}catch(Exception  e) { }
		
		Employee  e3=(Employee)session2.get(Employee.class, 7788);
		
		try {
			Thread.sleep(3000);
		}catch(Exception  e) { }
		
		Employee  e4=(Employee)session3.get(Employee.class, 7788);
		
		session1.close();
		session2.close();
		session3.close();
		factory.close();
		
	}
}
