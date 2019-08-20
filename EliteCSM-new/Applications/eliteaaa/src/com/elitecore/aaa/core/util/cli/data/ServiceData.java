package com.elitecore.aaa.core.util.cli.data;

import java.util.Date;



public class ServiceData {
	
	private final String name;
	private final String status;
	private final String socketAddress;	
	private final Date startDate;
	private final String remarks;
	
	public ServiceData(String name, String status, String socketAddress, Date startDate, String remarks) {
		this.name = name;
		this.status = status;
		this.socketAddress = socketAddress;		
		this.startDate = startDate;
		this.remarks= remarks;
	}
	
	public String getName() {
		return name;
	}
	public String getStatus() {
		return status;
	}
	public String getSocketAddress() {
		return socketAddress;
	}
	public Date getStartDate(){
		return startDate;
	}
	public String getRemarks(){
		return remarks;
	}
	
}
