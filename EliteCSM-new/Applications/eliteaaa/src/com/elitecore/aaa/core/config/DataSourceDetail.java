package com.elitecore.aaa.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class DataSourceDetail {

	private static final String DEFALUT_USERNAME_PASSWORD = "eliteaaa"; //NOSONAR - Reason: Credentials should not be hard-coded
	
	private String connectionUrl="jdbc:oracle:thin:@127.0.0.1:1521:eliteaaa";
	private String userName = DEFALUT_USERNAME_PASSWORD;
	private Password password;
	
	public DataSourceDetail(){
		//required by Jaxb.
		password = new Password(DEFALUT_USERNAME_PASSWORD);
	}

	@XmlElement(name = "connection-url",type = String.class,defaultValue ="jdbc:oracle:thin:@127.0.0.1:1521:eliteaaa")
	public String getConnectionUrlForAAADB() {
		return connectionUrl;
	}
	public void setConnectionUrlForAAADB(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	@XmlElement(name = "user-name",type = String.class,defaultValue = DEFALUT_USERNAME_PASSWORD)
	public String getUsernameForAAADB() {
		return userName;
	}
	public void setUsernameForAAADB(String userName) {
		this.userName = userName;
	}
	
	@XmlElement(name = "password",type = String.class,defaultValue = DEFALUT_USERNAME_PASSWORD)
	public String getPassword() {
		return password.getPassword();
	}
	
	public void setPassword(String password) {
		this.password.setPassword(password);
	}
	
	public String getPlainTextPassword(){
		return this.password.getPlainTextPassword();
	}
}
