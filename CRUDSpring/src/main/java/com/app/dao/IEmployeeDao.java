package com.app.dao;
import java.util.List;

import com.app.model.Employee;

public interface IEmployeeDao {

	public Integer saveEmployee(Employee e);
	public List<Employee> getAllEmployee();
	public void deleteEmployeeById(Integer empId);
}
