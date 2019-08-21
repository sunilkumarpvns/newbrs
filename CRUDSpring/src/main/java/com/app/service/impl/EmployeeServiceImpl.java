package com.app.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.IEmployeeDao;
import com.app.model.Employee;
import com.app.service.IEmployeeService;
@Service
public class EmployeeServiceImpl implements IEmployeeService
{
     @Autowired
     private IEmployeeDao dao;
	@Transactional
	public Integer saveEmployee(Employee e) {
		return dao.saveEmployee(e);
	}
	
	@org.springframework.transaction.annotation.Transactional(readOnly=true)
	public List<Employee> getAllEmployee() {
		return dao.getAllEmployee();
	}

	@Transactional
	public void deleteEmployeeById(Integer empId) {
		 dao.deleteEmployeeById(empId);
	}
	

}
