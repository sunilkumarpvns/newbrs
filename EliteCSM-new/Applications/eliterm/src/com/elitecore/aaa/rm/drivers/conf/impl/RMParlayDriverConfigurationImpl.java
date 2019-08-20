package com.elitecore.aaa.rm.drivers.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.rm.drivers.conf.RMParlayDriverConfiguration;


@XmlType(propOrder = {})
public class RMParlayDriverConfigurationImpl implements RMParlayDriverConfiguration {
	private static final String MODULE = "RMPARLY-DRVR-CONF-IMPL";
	private String driverInstanceId;
	private String driverName;
	private String webServiceAddress = "";
	private String userName = "";
	private String password = ""; //NOSONAR - Reason: Credentials should not be hard-coded
	private String translatonMappingName = "";
	private String sessionManagerServiceName;
	private String parlayServiceName;
	
	
	public RMParlayDriverConfigurationImpl() {
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setWebServiceAddress(String webServiceAddress) {
		this.webServiceAddress = webServiceAddress;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSessionManagerServiceName(String sessionManagerServiceName) {
		this.sessionManagerServiceName = sessionManagerServiceName;
	}

	public void setParlayServiceName(String parlayServiceName) {
		this.parlayServiceName = parlayServiceName;
	}

	@Override
	@XmlElement(name = "id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	@Override
	@XmlElement(name ="driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}

	@Override
	@XmlTransient
	public DriverTypes getDriverType() {		
		return DriverTypes.RM_PARLAY_DRIVER;
	}
	
	@Override
	@XmlElement(name ="password",type = String.class,defaultValue="")
	public String getPassword() {
		return password;
	}

	@Override
	@XmlElement(name ="translation-mapping-name",type = String.class,defaultValue="")
	public String getTranslationMappingName() {
		return translatonMappingName;
	}
	
	
	public void setTranslationMappingName(String translatonMappingName) {
		this.translatonMappingName = translatonMappingName;
	}

	@Override
	@XmlElement(name ="user-name",type = String.class,defaultValue="")
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	@XmlElement(name ="web-service-address",type = String.class,defaultValue="")
	public String getWebServiceAddress() {
		return webServiceAddress;
	}
	
	@Override
	@XmlElement(name ="parlay-service-name",type = String.class)
	public String getParlayServiceName() {
		return parlayServiceName; 
	}

	@Override
	@XmlElement(name ="sessionmanager-service-name",type = String.class)
	public String getSessionManagerServiceName() {
		return sessionManagerServiceName;
		
	}
	
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out= new PrintWriter(stringWriter);
		out.println();
		out.println();
		out.println(" -- RM PARLAY Driver Configuration -- ");
		out.println();
		out.println("    Driver Name 	              = " + driverName);
		out.println("    Driver InstanceId 	          = " + driverInstanceId);
		out.println("    Driver Type   	              = " + getDriverType().name());
		out.println("    Translation Mapping Name     = " + translatonMappingName);
		out.println("    WebService Address           = " + webServiceAddress);
		out.println("    UserName                     = " + userName);
		out.println("    Password                     =  *****");
		out.println("    Parlay Service Name          = "+ parlayServiceName);
		out.println("    Session Manager Service Name = "+ sessionManagerServiceName);
		out.println("    ");
		out.close();
		
		return out.toString();
	}
	
}
