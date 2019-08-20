package com.elite.config;

public class ORACLEConfig {
	private String datasourcename = "AUTH_SERVICE_DS";
	public String getDatasourcename() {
		return datasourcename;
	}
	public void setDatasourcename(String datasourcename) {
		this.datasourcename = datasourcename;
	}
	public String getConnectionurl() {
		return connectionurl;
	}
	public void setConnectionurl(String connectionurl) {
		this.connectionurl = connectionurl;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMinpoolsize() {
		return minpoolsize;
	}
	public void setMinpoolsize(int minpoolsize) {
		this.minpoolsize = minpoolsize;
	}
	public int getMaxpoolsize() {
		return maxpoolsize;
	}
	public void setMaxpoolsize(int maxpoolsize) {
		this.maxpoolsize = maxpoolsize;
	}
	private String connectionurl = "jdbc:oracle:thin:@192.168.14.101:1521:eliteaaa";
	private String username = "crestelaaapoc501";
	private String password = "crestelaaapoc501";
	private int minpoolsize = 10;
	private int maxpoolsize = 15;
	@Override
	public String toString() {
		String result = "=============ORACLEConfig================";
		result = result +"\r\n connectionurl = "+ connectionurl;
		result = result +"\r\n username = "+ username;
		result = result +"\r\n password = "+ "*******";
		result = result +"\r\n minpoolsize = "+ minpoolsize;
		result = result +"\r\n maxpoolsize = "+ maxpoolsize;
		result = result +"\r\n====================================";
		return result;
	}
}
