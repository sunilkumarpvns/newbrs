package com.elitecore.elitesm.web.systemstartup.defaultsetup.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.StringToIntegerAdapter;

@XmlRootElement(name = "default-configuration")
public class EliteAAASetupData {
	
	private String databaseDatasourceURL;
	private String databasePassword;
	private String databaseUsername;
	
	@NotEmpty(message = "User name must be specified.")
	@Pattern(regexp = "^[a-zA-Z0-9-@_.]*$", message = "Specify valid Username. Valid characters are : A-Z, a-z, 0-9, ., @, -, _")
	@Length(max = 64, message = "Username can be of 64 character only.")
	private String userName;
	
	@NotEmpty(message = "Password must be specified.")
	private String password;
	
	private String confirmPassword;
	private String macAddress;
	
	@Pattern(regexp = RestValidationMessages.IPV4_REGEX, message = "Specify Valid IP Address.")
	private String ipAddress;
	
	@NotNull(message = "Concurrent Login Limit must be specified.")
	@Range(min = 0, max = 99, message = "Specify valid Concurrent Login Limit. It must be two digit number i.e. number between 0 to 99 only.")
	
	private Integer concurrentLoginLimit;
	
	public String getDatabaseDatasourceURL() {
		return databaseDatasourceURL;
	}
	public void setDatabaseDatasourceURL(String databaseDatasourceURL) {
		this.databaseDatasourceURL = databaseDatasourceURL;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
	
	public String getDatabaseUsername() {
		return databaseUsername;
	}
	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}
	
	@XmlElement(name = "username")
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
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	@XmlElement(name = "mac-address")
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	@XmlElement(name = "ip-address")
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@XmlElement(name = "concurrent-login-limit")
	@XmlJavaTypeAdapter(StringToIntegerAdapter.class)
	public Integer getConcurrentLoginLimit() {
		return concurrentLoginLimit;
	}
	public void setConcurrentLoginLimit(Integer concurrentLoginLimit) {
		this.concurrentLoginLimit = concurrentLoginLimit;
	}
	
}
