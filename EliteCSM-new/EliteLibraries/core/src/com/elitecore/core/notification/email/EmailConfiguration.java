package com.elitecore.core.notification.email;


public class EmailConfiguration {

	private String mailFrom;
	private String host;
	private int port = 25;
	private boolean isAuthRequired;
	private String userName;
	private String password;
	
	public EmailConfiguration(String mailFrom, String host, int port, boolean isAuthRequired, String userName, String password) {
		this.mailFrom = mailFrom;
		this.host = host;
		this.port = port > 0 ? port : this.port;
		this.isAuthRequired = isAuthRequired;
		this.userName = userName;
		this.password = password;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public boolean isAuthRequired() {
		return isAuthRequired;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

}
