package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;

@XmlType(propOrder={})
public class DynAuthExternalSystemDetail {
	
	private boolean bAddEventTimeAttribute;
	private int actionOnSucess=1;
	private int actionOnFailure=1;
	private DynAuthDBScanDetail datasourceDetail;
	private DynAuthResponseCodeDetail responceCodeData;
		
	public  DynAuthExternalSystemDetail() {
		this.datasourceDetail = new DynAuthDBScanDetail();
		this.responceCodeData = new DynAuthResponseCodeDetail();
	}
	@Reloadable(type=DynAuthResponseCodeDetail.class)
	@XmlElement(name="response-code-to-retry")
	public DynAuthResponseCodeDetail getResponceCodeData() {
		return responceCodeData;
	}
	public void setResponceCodeData(DynAuthResponseCodeDetail responceCodeData) {
		this.responceCodeData = responceCodeData;
	}
	
	@Reloadable(type=DynAuthDBScanDetail.class)
	@XmlElement(name="scan-from-database")
	public DynAuthDBScanDetail getDatasourceDetail() {
		return datasourceDetail;
	}
	public void setDatasourceDetail(DynAuthDBScanDetail datasourceDetail) {
		this.datasourceDetail = datasourceDetail;
	}
	
	@Reloadable(type=Integer.class)
	@XmlElement(name="action-on-success",type=int.class)
	public int getActionOnSucess() {
		return actionOnSucess;
	}
	@Reloadable(type=Integer.class)
	@XmlElement(name="action-on-failure",type=int.class)
	public int getActionOnFailure() {
		return actionOnFailure;
	}
	
	public void setActionOnSucess(int actionOnSucess) {
		 this.actionOnSucess =actionOnSucess;
	}
	public void setActionOnFailure(int actionOnFailure) {
		this.actionOnFailure = actionOnFailure;
	}
	
	@XmlTransient
	public boolean getIsAddEventTimeAttribute() {
		return bAddEventTimeAttribute;
	}
	public void setIsAddEventTimeAttribute(boolean bAddEventTimeAttribute) {
		this.bAddEventTimeAttribute= bAddEventTimeAttribute;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		
		out.println("External System Detail");
		if(datasourceDetail!=null)
		out.println("    "+datasourceDetail);
		out.println("    Action-On-Success = "+getActionOnSucess());
		out.println("    Action-On-Failure = "+getActionOnFailure());
		if(responceCodeData!=null)
		out.println("    "+this.responceCodeData);
		out.close();
		return stringBuffer.toString();

		
	}


}
