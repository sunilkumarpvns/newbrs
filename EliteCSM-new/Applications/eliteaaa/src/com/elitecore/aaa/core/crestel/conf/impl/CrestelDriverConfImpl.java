package com.elitecore.aaa.core.crestel.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.XMLConfReader;
import com.elitecore.aaa.core.config.JNDIProprtyDetails;
import com.elitecore.aaa.core.crestel.conf.CrestelDriverConf;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

@XmlType(propOrder = {})
public abstract class CrestelDriverConfImpl implements CrestelDriverConf ,XMLConfReader{
	
	// Don't make MODULE static, because it should print in log according to particular driver. 
	protected String MODULE = "CRESTEL-DRV-CONF-IMPL";
	private String driverInstanceId;
	private String driverName="";
	
	public static final String RESPONCE_CODE_ATTRIBUTE_NAME = "RESPONSE-CODE";
	public static final int RESPONCE_CODE_SUCCESS = 1;

	
	public static final String FACTORY_INITIAL= "java.naming.factory.initial";
	public static final String FACTORY_URL_PKGS= "java.naming.factory.url.pkgs";
	public static final String PROVIDER_URL= "java.naming.provider.url";
	public static final String RESPONSE_TIME_CALC_REQUIRED = "response.time.calc.required";
	
	private Hashtable<String,String> jndiPropertyMap; 
	private String translationMappingConfName="";
	private JNDIProprtyDetails jndiProprtyDetails;

	public CrestelDriverConfImpl() {
		jndiPropertyMap=new Hashtable<String,String>();
		this.jndiProprtyDetails = new JNDIProprtyDetails();
	}

	

	@Override
	public void readConfigurationFromXML() throws LoadConfigurationException {
	}
	
	@Override
	@XmlElement(name = "id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId =driverInstanceId;;
	}
	
	@Override
	@XmlTransient
	public Hashtable<String,String> getJndiPropertyMap(){
		return this.jndiPropertyMap;
	}
	
	public void setJndiPropertyMap(Hashtable<String,String> jndiPropertyMap){
		this.jndiPropertyMap = jndiPropertyMap;
	}
	
	@Override
	@XmlElement(name ="translation-mapping-name",type = String.class,defaultValue="")
	public String getTranslationMappingName() {
		return translationMappingConfName;
	}
	
	
	public void setTranslationMappingName(String translatonMappingName) {
		this.translationMappingConfName = translatonMappingName;
	}

	@Override
	@XmlElement(name ="driver-name",type = String.class)
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	@XmlElement(name ="jndi-properties")
	public JNDIProprtyDetails getJndiProprtyDetails() {
		return jndiProprtyDetails;
	}
	public void setJndiProprtyDetails(JNDIProprtyDetails jndiProprtyDetails) {
		this.jndiProprtyDetails = jndiProprtyDetails;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println(" -- "+getDriverType().name()+" -- ");
		out.println();
		out.println("    Driver Name 	           = " + driverName);
		out.println("    Driver InstanceId 	       = " + driverInstanceId);
		out.println("    Translation Mapping Name  = " + translationMappingConfName);
		  out.print("    Jndi Properties          : ");
		  out.print( jndiPropertyMap);
		  out.println("    ");
		  out.close();
		return stringBuffer.toString();
	}
}
