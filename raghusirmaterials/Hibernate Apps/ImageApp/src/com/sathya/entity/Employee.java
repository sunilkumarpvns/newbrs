package com.sathya.entity;

import java.sql.Blob;

public class Employee {
	private  int  empno;
	private Blob  image;

	
	public int getEmpno() {
		return empno;
	}
	public void setEmpno(int empno) {
		this.empno = empno;
	}
	public Blob getImage() {
		return image;
	}
	public void setImage(Blob image) {
		this.image = image;
	}
	

}
