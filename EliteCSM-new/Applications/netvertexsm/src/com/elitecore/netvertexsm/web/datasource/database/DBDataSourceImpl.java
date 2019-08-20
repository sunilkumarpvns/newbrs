package com.elitecore.netvertexsm.web.datasource.database;

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
	private String password = "admin";
	private int minPoolSize = 2;
	private int maxPoolSize = 5;
	private int statusCheckDuration;
	public static final int DEFAULT_STATUS_CHECK_DURATION = 120;
	private int networkReadTimeoutInMs = EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue;

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
	  this.password = password;
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
		this.password = password;
	}
	public void setMinimumPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	public void setMaximumPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	
	
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	public String getDatasourceID() {
		return dsID;
	}

	public String getConnectionURL() {
		return connURL;
	}

	public String getUsername() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getPlainTextPassword() {
		//TODO returning the same password, return the decrypted password when implementation is done
		return password;
	}
	
	public String getDataSourceName() {
		return dsName;
	}

	public int getMinimumPoolSize() {
		return minPoolSize;
	}

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
