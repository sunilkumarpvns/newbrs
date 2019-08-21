package com.sathya.test;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sathya.util.HibernateUtil;

public class QueryCacheTest 
{
	public static void main(String args[])
	{
		SessionFactory  factory=HibernateUtil.getSessionFactory();
		Session   session1=factory.openSession();
		Session   session2=factory.openSession();
		
		Query   qry1 = session1.createQuery("from  Employee  e  where  e.deptNumber=?");
		qry1.setParameter(0, 20);
		qry1.setCacheable(true);
		List  list1 = qry1.list();
		System.out.println("============");
		
		Query   qry2 = session2.createQuery("from  Employee  e  where  e.deptNumber=?");
		qry2.setParameter(0, 20);
		qry2.setCacheable(true);
		List  list2 = qry2.list();
		
		session1.close();
		session2.close();
		factory.close();
		
	}

}
