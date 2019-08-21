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
@Table(name="stdtab")
public class Student 
{
@Id
@Column(name="sid")
@GeneratedValue(generator="sgen")
@GenericGenerator(name="sgen",strategy="increment")
private Integer stdId;

@Column(name="sname")
private String stdName;
@Column(name="ssal")
private Double stdSal;

@Column(name="spwd")
private String stdPwd;
@Column(name="sgen")
private String stdGen;
@Column(name="saddr")
private String stdAddr;

@ElementCollection(fetch=FetchType.EAGER)
@CollectionTable(name="std_lang_tab",
	joinColumns=@JoinColumn(name="sidFk")
)
@Column(name="lang")
@OrderColumn(name="pos")
private List<String> stdLang=new ArrayList<String>(0);

@Column(name="scntry")
private String stdCntry;

public Student() {
	super();
}

public Student(Integer stdId) {
	super();
	this.stdId = stdId;
}

public Integer getStdId() {
	return stdId;
}

public void setStdId(Integer stdId) {
	this.stdId = stdId;
}

public String getStdName() {
	return stdName;
}

public void setStdName(String stdName) {
	this.stdName = stdName;
}

public Double getStdSal() {
	return stdSal;
}

public void setStdSal(Double stdSal) {
	this.stdSal = stdSal;
}

public String getStdPwd() {
	return stdPwd;
}

public void setStdPwd(String stdPwd) {
	this.stdPwd = stdPwd;
}

public String getStdGen() {
	return stdGen;
}

public void setStdGen(String stdGen) {
	this.stdGen = stdGen;
}

public String getStdAddr() {
	return stdAddr;
}

public void setStdAddr(String stdAddr) {
	this.stdAddr = stdAddr;
}

public List<String> getStdLang() {
	return stdLang;
}

public void setStdLang(List<String> stdLang) {
	this.stdLang = stdLang;
}

public String getStdCntry() {
	return stdCntry;
}

public void setStdCntry(String stdCntry) {
	this.stdCntry = stdCntry;
}

@Override
public String toString() {
	return "Student [stdId=" + stdId + ", stdName=" + stdName + ", stdSal=" + stdSal + ", stdPwd=" + stdPwd
			+ ", stdGen=" + stdGen + ", stdAddr=" + stdAddr + ", stdLang=" + stdLang + ", stdCntry=" + stdCntry + "]";
}


}
