package com.elite.config;

import netscape.ldap.LDAPConstraints;

import com.elite.exception.CommunicationException;

public class LDAPConfig {
	public String getHost_ip() {
		return host_ip;
	}
	public void setHost_ip(String host_ip) {
		this.host_ip = host_ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getSize_limit() {
		return size_limit;
	}
	public void setSize_limit(String size_limit) {
		this.size_limit = size_limit;
	}
	public String getAdministrator() {
		return administrator;
	}
	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMin_con() {
		return min_con;
	}
	public void setMin_con(int min_con) {
		this.min_con = min_con;
	}
	public int getMax_con() {
		return max_con;
	}
	public void setMax_con(int max_con) {
		this.max_con = max_con;
	}
	public String getSearch_base_dn() {
		return search_base_dn;
	}
	public void setSearch_base_dn(String search_base_dn) {
		this.search_base_dn = search_base_dn;
	}
	public String getUser_base_dn() {
		return user_base_dn;
	}
	public void setUser_base_dn(String user_base_dn) {
		this.user_base_dn = user_base_dn;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	private String host_ip = null;
	private int port = 0;
	private int timeout = 0;
	private String size_limit = null;
	private String administrator = null;
	private String password = null;
	private int min_con = 0;
	private int max_con = 0;
	private String search_base_dn = null;
	private String user_base_dn = null;
	private String user_password = null;
	private LDAPConstraints constraints = new LDAPConstraints();
	
	public void validate() throws CommunicationException
	{
		if(host_ip == null || port == 0  || timeout == 0 ||	size_limit == null || administrator == null || password == null)
		{
			throw new CommunicationException("Some parameter or parameters are not specified for the ldap configuration.");
		}
		constraints.setTimeLimit(timeout);
	}
	@Override
	public String toString() {
		String result = "===============LDAPConfig================";
		result = result +"\r\n host_ip = "+ host_ip;
		result = result +"\r\n port = "+ port;
		result = result +"\r\n timeout = "+ timeout;
		result = result +"\r\n size_limit = "+ size_limit;
		result = result +"\r\n administrator = "+ administrator;
		result = result +"\r\n password = "+ "******";
		result = result +"\r\n min_con = "+ min_con;
		result = result +"\r\n max_con = "+ max_con;
		result = result +"\r\n search_base_dn = "+ search_base_dn;
		result = result +"\r\n user_base_dn = "+ user_base_dn;
		result = result +"\r\n user_password = "+ "******";
		result = result +"\r\n====================================";
		return result;
	}
}
