package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.UserFileDriverConfiguration;

public abstract class UserFileAuthDriverConfigurationImpl implements UserFileDriverConfiguration{
	
	public static final String DRIVER = "driver";
	private String driverInstanceId;
	private String driverName="";
	private String[] locations;
	private SimpleDateFormat[] expiryPatterns; 
	private String userFileLocations="data/userfiles";
	private String strExpiryPatterns ="MM/dd/yyyy";


	public UserFileAuthDriverConfigurationImpl(){
		this.expiryPatterns = new SimpleDateFormat[1];
	}

	@XmlElement(name = "expiry-date-formats",type = String.class,defaultValue ="MM/dd/yyyy")
	public String getStrExpiryPatterns() {
		return strExpiryPatterns;
	}
	public void setStrExpiryPatterns(String expriryPatterns) {
		this.strExpiryPatterns = expriryPatterns;
	}

	
	public static String getDriver() {
		return DRIVER;
	}

	@XmlTransient
	public String[] getLocations() {
		return this.locations;
	}

	@XmlTransient
	public SimpleDateFormat[] getexpiryPatterns() {
		return this.expiryPatterns;
	} 

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- User Auth File Driver Configuration -- ");
		out.println();
		out.println("      -------------------------------------");
		out.println("    Userfile Locations 	 = " + userFileLocations);
		out.print  ("    Expiry Date Patterns =");
		for(int i=0;i<expiryPatterns.length;i++){
			out.print("  " + expiryPatterns[i].toPattern());
		}
		out.println();
		out.println("      -------------------------------------");
		out.println("    ");

		out.close();
		return stringBuffer.toString();
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

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setLocations(String[] locations) {
		this.locations = locations;
	}

	public void setExpiryPatterns(SimpleDateFormat[] expiryPatterns) {
		this.expiryPatterns = expiryPatterns;
	}

	public void setUserFileLocation(String userFileLocations) {
		this.userFileLocations = userFileLocations;
	}

	@XmlElement(name = "file-locations",type = String.class,defaultValue="data/userfiles")
	public String getUserFileLocation(){
		return userFileLocations;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof UserFileAuthDriverConfigurationImpl))
			return false;
		
		UserFileAuthDriverConfigurationImpl other = (UserFileAuthDriverConfigurationImpl) obj;
		return this.driverInstanceId == other.driverInstanceId;
	}
}
