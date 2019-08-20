package com.elitecore.elitesm.web.dashboard.db;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DBConfiguration {
	private String connectionUrl;
	private String driverClass;
	private String userName;
	private String password;
	
	public  DBConfiguration(String driverClass,String connectionUrl,String userName, String password){
		this.driverClass=driverClass;
		this.connectionUrl=connectionUrl;
		this.userName = userName;
		this.password = password;
	}
	
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println(" -- Database Configuration -- ");
		out.println();
		out.println("      Connection URL = " + connectionUrl);
		out.println("      Driver Class   = " + driverClass);
		out.println("      User Name      = " + userName);
		out.println();
		out.println();
		out.close();
		return stringBuffer.toString();
	}
}
