package com.sathya.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="emp1")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY,region="employee")
public class Employee 
{
	@Id
	@Column(name="empno")
	private  int  empNumber;
	
	@Column(name="ename")
	private  String  empName;
	
	@Column(name="sal")
	private  int   empSal;
	
	@Column(name="deptno")
	private  int   deptNumber;
	
	public  Employee()
	{  }
	
	public  Employee(int  empNumber)
	{
		this.empNumber=empNumber;
	}
	
	public  Employee(int  empNumber,String empName,int empSal,int  deptNumber)
	{
		this.empNumber=empNumber;
		this.empName=empName;
		this.empSal=empSal;
		this.deptNumber=deptNumber;
	}
	
	
	

	public int getEmpNumber() {
		return empNumber;
	}

	public void setEmpNumber(int empNumber) {
		this.empNumber = empNumber;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public int getEmpSal() {
		return empSal;
	}

	public void setEmpSal(int empSal) {
		this.empSal = empSal;
	}

	public int getDeptNumber() {
		return deptNumber;
	}

	public void setDeptNumber(int deptNumber) {
		this.deptNumber = deptNumber;
	}

	
	public   String    toString()
	{
		return  "Employee["+empNumber+" , "+empName+" , "+empSal+" , "+deptNumber+"]";
	}
	
}
