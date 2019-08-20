package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"connectionUrl","userName","password"})
public class DataSourceDetailData {

	private static final String DEFALUT_USERNAME_PASSWORD = "ELITEAAA";
	
	private String connectionUrl;
	private String userName;
	private String password;
	
	public DataSourceDetailData(){
		password = DEFALUT_USERNAME_PASSWORD;
		connectionUrl="jdbc:oracle:thin:@127.0.0.1:1521:eliteaaa";
		userName = DEFALUT_USERNAME_PASSWORD;
	}

	@XmlElement(name = "connection-url")
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	@XmlElement(name = "user-name")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
