package com.app.dao;

import java.util.List;

import com.app.model.Employee;

public interface IEmployeeDao {

	public Integer saveEmployee(Employee e);
	public void updateEmployee(Employee e);
	public void deleteEmployeeById(Integer empId);
	
	public Employee getEmployeeById(Integer empId);
	public List<Employee> getAllEmployees();
	
	
	
	
}
