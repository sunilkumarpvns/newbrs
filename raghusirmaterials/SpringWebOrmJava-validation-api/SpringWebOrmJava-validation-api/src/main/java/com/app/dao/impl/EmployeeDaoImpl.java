package com.app.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.IEmployeeDao;
import com.app.model.Employee;

@Repository
public class EmployeeDaoImpl implements IEmployeeDao {
	
	@Autowired
	private SessionFactory sf;
	
	@Override
	public Integer saveEmployee(Employee e) {
		return (Integer)sf.getCurrentSession().save(e);
	}
	
	@Override
	public void updateEmployee(Employee e) {
		sf.getCurrentSession().update(e);
	}

	
	@Override
	public Employee getEmployeeById(Integer empId) {
		return sf.getCurrentSession().get(Employee.class, empId);
	}
	
	
	@Override
	public void deleteEmployeeById(Integer empId) {
		sf.getCurrentSession().delete(new Employee(empId));
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		return sf.getCurrentSession().createQuery("from "+Employee.class.getName()).list();
	}
	
}
