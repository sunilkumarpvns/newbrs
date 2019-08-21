package com.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="emptab")
public class Employee {
	@Id
	@Column(name="eid")
	@GeneratedValue(generator="empgen")
	@GenericGenerator(name="empgen",strategy="increment")
	private Integer empId;
	
	
	@Column(name="ename")
	private String empName;
	@Column(name="esal")
	private Double empSal;
	
	@Column(name="epwd")
	private String empPwd;
	@Column(name="egen")
	private String empGen;
	@Column(name="eaddr")
	private String empAddr;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name="emp_lang_tab",
		joinColumns=@JoinColumn(name="eidFk")
	)
	@Column(name="lang")
	@OrderColumn(name="pos")
	private List<String> empLang=new ArrayList<String>(0);
	
	@Column(name="ecntry")
	private String empCntry;
	
	public Employee() {
		super();
	}

	public Employee(Integer empId) {
		super();
		this.empId = empId;
	}

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public Double getEmpSal() {
		return empSal;
	}

	public void setEmpSal(Double empSal) {
		this.empSal = empSal;
	}

	public String getEmpPwd() {
		return empPwd;
	}

	public void setEmpPwd(String empPwd) {
		this.empPwd = empPwd;
	}

	public String getEmpGen() {
		return empGen;
	}

	public void setEmpGen(String empGen) {
		this.empGen = empGen;
	}

	public String getEmpAddr() {
		return empAddr;
	}

	public void setEmpAddr(String empAddr) {
		this.empAddr = empAddr;
	}

	public List<String> getEmpLang() {
		return empLang;
	}

	public void setEmpLang(List<String> empLang) {
		this.empLang = empLang;
	}

	public String getEmpCntry() {
		return empCntry;
	}

	public void setEmpCntry(String empCntry) {
		this.empCntry = empCntry;
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", empName=" + empName + ", empSal=" + empSal + ", empPwd=" + empPwd
				+ ", empGen=" + empGen + ", empAddr=" + empAddr + ", empLang=" + empLang + ", empCntry=" + empCntry
				+ "]";
	}
	
	
}
