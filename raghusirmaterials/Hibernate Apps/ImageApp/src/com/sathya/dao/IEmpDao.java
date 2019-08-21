package com.sathya.dao;

import com.sathya.entity.Employee;

public interface IEmpDao 
{
	 void  saveEmp(Employee  emp);
	 Employee  readEmp(int  empno);

}
