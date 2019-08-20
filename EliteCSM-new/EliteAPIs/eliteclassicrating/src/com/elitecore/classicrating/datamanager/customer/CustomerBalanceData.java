/**
 *   Modified by Raghu
 *   Renamed servicetype  as customerIdentifier as per the database.
 */
package com.elitecore.classicrating.datamanager.customer;

/**
 * @author sheetalsoni
 *
 */
public class CustomerBalanceData {
	
	private String customerName;              
	private String customerIdetiifer;                
	private double balance;
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerIdetiifer() {
		return customerIdetiifer;
	}
	public void setCustomerIdetiifer(String customerIdetiifer) {
		this.customerIdetiifer = customerIdetiifer;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
}
