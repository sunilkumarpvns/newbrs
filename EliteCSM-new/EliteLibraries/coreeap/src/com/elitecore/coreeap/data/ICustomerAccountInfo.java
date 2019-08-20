package com.elitecore.coreeap.data;

public interface ICustomerAccountInfo {
	public String getUserName();
	public String getPassword();
	public String getPasswordCheck();
	public String getEncryptionType();
	public String getUserIdentity();
	public boolean isMacValidation();
	
}