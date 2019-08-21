package com.sathya.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sathya.util.HibernateUtil;

public class Test {

	public static void main(String[] args) {
		SessionFactory   factory=HibernateUtil.getSessionFactory();
		Session  session = factory.openSession();
		session.doWork(new  JdbcWork());
		session.close();

	}

}
