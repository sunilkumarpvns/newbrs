package com.sathya.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sathya.entity.Employee;
import com.sathya.util.HibernateUtil;

public class EmpDaoImpl implements IEmpDao
{
	private  SessionFactory  factory;
	public EmpDaoImpl() {
		factory = HibernateUtil.getSessionFactory();
	}

	@Override
	public void saveEmp(Employee emp) {
		Session  ses = factory.openSession();
		Transaction tx = ses.beginTransaction();
		ses.save(emp);
		tx.commit();
		ses.close();
	}

	@Override
	public Employee readEmp(int empno) {
		Session  ses = factory.openSession();
		Employee  e=(Employee)ses.get(Employee.class, empno);
		ses.close();
		return e;
	}

}



