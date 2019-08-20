package com.elitecore.netvertexsm.ws.db;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DBConfiguration {
	private String name;
	private String connectionUrl;
	private String driverClass;
	private String username;
	private String password;
	private int minPoolSize = 5;
	private int maxPoolSize = 70;
	
	public  DBConfiguration(String name, String driverClass,String connectionUrl,String username, String password){
		this.name = name;
		this.driverClass=driverClass;
		this.connectionUrl=connectionUrl;
		this.username = username;
		this.password = password;
	}
	
	public DBConfiguration(String name, String connectionUrl,
				String driverClass, String username, String password,
				int minPoolSize, int maxPoolSize) {
		this(name, driverClass, connectionUrl, username, password);
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
	}
	
	public String getName() {
		return name;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public String getUserName() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public int getMinPoolSize() {
		return minPoolSize;
	}
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		out.println();
		out.println(" -- Database Configuration -- ");
		out.println();
		out.println("      Name = " + name);
		out.println("      Connection URL = " + connectionUrl);
		out.println("      Driver Class   = " + driverClass);
		out.println("      Username      = " + username);
		out.println("      Min Pool Size  = " + minPoolSize);
		out.println("      Max Pool Size  = " + maxPoolSize);
		out.close();
		return stringWriter.toString();
	}
}
