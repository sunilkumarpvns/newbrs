package com.elitecore.aaa.core.config;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.core.commons.util.db.EliteDBConnectionProperty;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;

public class DBDataSourceImpl implements DBDataSource{

	private String dsID;
	private  String dsName;
	private  String connURL ="jdbc:oracle:thin:@ipaddress:port:sid";;
	private  String userName ="admin";
	private Password password;
	private int minPoolSize = 2;
	private int maxPoolSize = 5;
	private int statusCheckDuration;
	public static final int DEFAULT_STATUS_CHECK_DURATION = 120;
	private int networkReadTimeoutInMs = EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue;

	public DBDataSourceImpl(){
		//required by Jaxb.
		password = new Password();
	}
	
	/**
	  * This will be used as backword compatibility
	  * as groovy plugin will be initilize using this
	  * constructor.
	  * 
	  * @param dsID
	  * @param dsName
	  * @param connURL
	  * @param userName
	  * @param password
	  * @param minPoolSize
	  * @param maxPoolSize
	  * @param statusCheckDuration
	  */
	 @Deprecated
	 public DBDataSourceImpl(String dsID, String dsName, String connURL, String userName, String password, int minPoolSize, int maxPoolSize, int statusCheckDuration) {
	  this.dsID = dsID;
	  this.dsName = dsName;
	  this.connURL = connURL;
	  this.userName = userName;
	  this.password = new Password(password);
	  this.minPoolSize = minPoolSize;
	  this.maxPoolSize = maxPoolSize;
	  this.statusCheckDuration = statusCheckDuration;
	 }
	 
	 public DBDataSourceImpl(String dsID, String dsName, String connURL, String userName, String password, int minPoolSize, int maxPoolSize, int statusCheckDuration,int networkReadTimeout) {
		  this(dsID, dsName, connURL, userName, password, minPoolSize, maxPoolSize, statusCheckDuration);
		  this.networkReadTimeoutInMs = networkReadTimeout;
	 }

	public void setDatasourceID(String dsID) {
		this.dsID = dsID;
	}
	public void setDataSourceName(String dsName) {
		this.dsName = dsName;
	}
	public void setConnectionURL(String connURL) {
		this.connURL = connURL;
	}
	public void setUsername(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password.setPassword(password);
	}
	public void setMinimumPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public void setMaximumPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	
	
	@XmlElement(name = "status-check-duration",type = int.class, defaultValue="120")
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	@XmlElement(name = "id",type = String.class)
	public String getDatasourceID() {
		return dsID;
	}

	@XmlElement(name = "connection-url",type = String.class,defaultValue ="jdbc:oracle:thin:@127.0.0.1:1521:eliteaaa")
	public String getConnectionURL() {
		return connURL;
	}
	@XmlElement(name = "username",type = String.class,defaultValue ="admin")
	public String getUsername() {
		return userName;
	}
	
	@XmlElement(name = "password",type = String.class,defaultValue ="admin")
	public String getPassword() {
		return password.getPassword();
	}
	
	@Override
	public String getPlainTextPassword(){
		return password.getPlainTextPassword();
	}

	@XmlElement(name = "datasource",type = String.class)
	public String getDataSourceName() {
		return dsName;
	}
	@XmlElement(name = "minimum-pool-size",type = int.class,defaultValue ="2")
	public int getMinimumPoolSize() {
		return minPoolSize;
	}
	@XmlElement(name = "maximum-pool-size",type = int.class,defaultValue ="5")
	public int getMaximumPoolSize() {
		return maxPoolSize;
	}
	
	public void setStatusCheckDuration(int statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	@XmlElement(name = "network-read-timeout",type = int.class,defaultValue= "3000")
	public int getNetworkReadTimeout() {
		return networkReadTimeoutInMs;
	}
	
	public void setNetworkReadTimeout(int networkReadTimeout) {
		 this.networkReadTimeoutInMs = networkReadTimeout;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    Datasource Name = " + dsName);
		out.println("    Connection URL = " + connURL);
		out.println("    User Name = " + userName);
		out.println("    Password = *****");
		out.println("    Minimum Pool Size = " + minPoolSize);
		out.println("    Maximum Pool Size = " + maxPoolSize);
		out.println("    Status Check Duration = " + statusCheckDuration);
		out.println("    Network Read Timeout = " + networkReadTimeoutInMs);
		out.close();
		return stringBuffer.toString();
	}

}
