package com.elitecore.netvertex.cli.data;

import java.util.Date;



public class ServiceData {
	
	private String name;
	private String status;
	private String serviceAddress;	
	private int port;
	
	private Date startDate = null;
	private String remarks;
	
	public ServiceData(String name, String status, String serviceAddress, Date startDate, String remarks, int port) {
		this.name = name;
		this.status = status;
		this.serviceAddress = serviceAddress;
		this.port = port;
	
		this.startDate = startDate;
		this.remarks= remarks;
	}
	
	public String getName() {
		return name;
	}
	public String getStatus() {
		return status;
	}
	public String getServiceAddress() {
		return serviceAddress;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	public String getRemarks(){
		return remarks;
	}
	public int getPort() {
		return port;
	}
	
}
