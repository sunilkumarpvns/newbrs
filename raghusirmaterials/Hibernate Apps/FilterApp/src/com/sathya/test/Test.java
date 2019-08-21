package com.sathya.test;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sathya.entity.Flight;
import com.sathya.util.HibernateUtil;

public class Test {
	public  static void main(String args[ ])
	{
		SessionFactory  factory=HibernateUtil.getSessionFactory();
		
		Session   session=factory.openSession();
	    Query   query=session.createQuery("from  Flight  f");
		 
		 //enable filter(statusFilter1)
		 Filter  filter = session.enableFilter("statusFilter1");
		 Scanner  s = new  Scanner(System.in);
		 System.out.println("enter flight status");
		 String  str = s.next();
		 filter.setParameter("flightStatus", str);
		 System.out.println("--printing Flights with status : "+str+"----------");
		 List   list = query.list();
		 Iterator  it = list.iterator();
		 while(it.hasNext())
		 {
			 Flight  flight =(Flight)it.next();
			 System.out.println(flight);
		 }
		 System.out.println("=================");
		 
		 
		 //disable filter(statusFilter1)
		 session.disableFilter("statusFilter1");
		 		 
		 //enable filter(statusFilter2)
		 Filter   filter2 = session.enableFilter("statusFilter2");
		 System.out.println("enter  flight status1 ");
		 String   status1 = s.next();
		 System.out.println("enter  flight  status2 ");
		 String   status2 = s.next();
		 
		 filter2.setParameter("flightStatus1", status1);
		 filter2.setParameter("flightStatus2", status2);
		 
		 System.out.println("----Flights  with status: "+status1+" or "+status2+"----");
		 List   list2 = query.list();
		 Iterator  it2 = list2.iterator();
		 while(it2.hasNext())
		 {
			 Flight  flight =(Flight)it2.next();
			 System.out.println(flight);
		 }
		 
		 session.close();
		 factory.close();
	}

}
