package com.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dao.IEmployeeDao;
import com.app.model.Employee;
import com.app.service.IEmployeeService;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
	
	@Autowired
	private IEmployeeDao dao;
	
	@Transactional
	public Integer saveEmployee(Employee e) {
		return dao.saveEmployee(e);
	}
	
	@Transactional
	public void updateEmployee(Employee e) {
		dao.updateEmployee(e);
	}


	@Transactional
	public void deleteEmployeeById(Integer empId) {
		dao.deleteEmployeeById(empId);
	}
	
	@Transactional(readOnly=true)
	public Employee getEmployeeById(Integer empId) {
		return dao.getEmployeeById(empId);
	}
	
	@Transactional(readOnly=true)
	public List<Employee> getAllEmployees() {
		return dao.getAllEmployees();
	}
}
