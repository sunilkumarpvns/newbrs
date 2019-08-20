/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu G
 *  Last Modified October 3, 2008 
 *  Added new Package definition object and prefix group
 */

/*
 * CustomerServiceData.java 
 * This class is an POJO class used to represent an customer service data.
 * It contains declaration for all attributes with appropriate set and get methods.
 * 
 */

package com.elitecore.classicrating.datamanager.customer;

import com.elitecore.classicrating.datamanager.packages.PackageDefinitionData;


/**
 * @author raghug
 *
 */
public class CustomerServiceData {

	private int packageId;           

	private String customerName;      
	private String customerIdentifier;
	private String serviceType;       
	private String customerType;     
	private String customerSubType; 
	private String version;
	
	private String requestType;
	
	private long totalUsage;       
	
	// Other Objects
	private PackageDefinitionData packageDefinitionData;
	private CustomerBalanceData customerBalanceData;
	
	public CustomerServiceData(){
	}

	public String toString() {
		String returnString = new String("Customer Service Data = [ " +
										 "Customer Identifier = " + customerIdentifier + ", " +
										 "Customer Name = " + customerName + ", " +
										 "Customer Sub Type = " + customerSubType + ", " +
										 "Customer Type = " + customerType + ", " +
										 "Package Id = " + packageId + ", " +
										 "Request Type = " + requestType + ", " +
										 "Service Type = " + serviceType + ", " +
										 "Total Usage = " + totalUsage + ", " +
										 "Version = " + version + ", ");
		if(customerBalanceData != null)
			returnString += customerBalanceData + ", ";
		else
			returnString += "Customer Balance Data = [ ], ";
						  
		if(packageDefinitionData != null)
			returnString += packageDefinitionData + " ]";
		else
			returnString += "Package Defintion Data = [ ] ]";
		
		return returnString;
	}
	
	public String getCustomerIdentifier() {
		return customerIdentifier;
	}
	public void setCustomerIdentifier(String customerIdentifier) {
		this.customerIdentifier = customerIdentifier;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCustomerSubType() {
		return customerSubType;
	}
	public void setCustomerSubType(String customerSubType) {
		this.customerSubType = customerSubType;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public int getPackageId() {
		return packageId;
	}
	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
	public long getTotalUsage() {
		return totalUsage;
	}
	public void setTotalUsage(long totalUsage) {
		this.totalUsage = totalUsage;
	}           
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	
	// added for package object
	
	public void setPackageDefinitionData(PackageDefinitionData packageData){
		this.packageDefinitionData = packageData;
	}
	
	public PackageDefinitionData getPackageDefinitionData(){
		return packageDefinitionData;
	}
	
	/**
	 * @return the customerBalanceData
	 */
	public CustomerBalanceData getCustomerBalanceData() {
		return customerBalanceData;
	}

	/**
	 * @param customerBalanceData the customerBalanceData to set
	 */
	public void setCustomerBalanceData(CustomerBalanceData customerBalanceData) {
		this.customerBalanceData = customerBalanceData;
	}
}
