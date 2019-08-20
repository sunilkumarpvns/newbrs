package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.VSAInClassConfiguration;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "vsa-in-class-attribute")
@ConfigurationProperties(moduleName="VSAIN_CLASSATTR_CONFIGURABLE",synchronizeKey ="VSA_IN_CLASS", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf"},name = "vsa-in-class-attribute")
public class VSAInClassConfigurable extends Configurable implements VSAInClassConfiguration {
	private static final String MODULE = "VSA-IN-CLASS-CONFIGURABLE";
	
	private String strPrefix = "ELITECLASS";
	private String[] classAttributeId = {"0:25"};
	private String classAttributeIdString;
	private boolean isEnabled;
	private char   strSeparator=',';
	private String stringOfSeparator =",";
	private List<KeyValuePair>  attrbiutesFromReqeustPacket = new ArrayList<KeyValuePair>();
	private List<KeyValuePair>  attributesFromResponsePacket = new ArrayList<KeyValuePair>();
	private String requestPacketString;
	private String responsePacketString;

	
	@XmlElement(name = "separator",type = String.class,defaultValue =",")
	public String getStringOfSeparator() {
		return stringOfSeparator;
	}
	public void setStringOfSeparator(String stringOfSeparator) {
		this.stringOfSeparator = stringOfSeparator;
	}
	
	@XmlElement(name = "enabled",type = boolean.class)
	public boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@XmlTransient
	public char getSeparator() {
		return strSeparator;
	}
	public void setSeparator(char strSeparator) {
		this.strSeparator = strSeparator;
	}
	
	@XmlElement(name = "class-attribute-id",type = String.class)
	public String getClassAttributeIdString() {
		return classAttributeIdString;
	}
	public void setClassAttributeIdString(String classAttributeIdString) {
		this.classAttributeIdString = classAttributeIdString;
	}

	@Reloadable(type=String.class)
	@XmlElement(name = "attributes-from-request-packet",type = String.class)
	public String getRequestPacketString() {
		return requestPacketString;
	}
	public void setRequestPacketString(String requestPacketString) {
		this.requestPacketString = requestPacketString;
	}
	
	@Reloadable(type=String.class)
	@XmlElement(name = "attributes-from-response-packet",type = String.class)
	public String getResponsePacketString() {
		return responsePacketString;
	}
	public void setResponsePacketString(String responsePacketString) {
		this.responsePacketString = responsePacketString;
	}

	@PostRead
	@PostReload
	public void doProcessing() {
		if (this.isEnabled) {
			
			String[] tempClassAttributeId = {"0:25"};
			
			if (Strings.isNullOrBlank(classAttributeIdString) == false) {
				tempClassAttributeId = getTrimedStringArray(classAttributeIdString.trim().split(","));
			} else {
				LogManager.getLogger().warn(MODULE, "VSA in class feature is enabled but no class attribute id is"
						+ " configured. Using default value: " + Arrays.toString(tempClassAttributeId));
			}
			
			this.classAttributeId = tempClassAttributeId;
			
			this.strSeparator = this.stringOfSeparator.charAt(0);
			
			
			if (this.requestPacketString != null) {
				this.attrbiutesFromReqeustPacket = getKeyValuePairArray(this.requestPacketString.trim().split(","));
			}
			List<KeyValuePair>  tempAttributesFromResponsePacket = null;
			if(this.responsePacketString!=null){
				tempAttributesFromResponsePacket = getKeyValuePairArray(this.responsePacketString.trim().split(","));	
			}
			
			
			if(tempAttributesFromResponsePacket==null){
				tempAttributesFromResponsePacket = new ArrayList<KeyValuePair>();
				tempAttributesFromResponsePacket.add(new KeyValuePair("0:25", "0:25", null));
			}else{
				if(!tempAttributesFromResponsePacket.contains(new KeyValuePair("0:25", "0:25", null))){
					tempAttributesFromResponsePacket.add(new KeyValuePair("0:25", "0:25", null));
				}
			}
			this.attributesFromResponsePacket = tempAttributesFromResponsePacket;		
			
			LogManager.getLogger().info(MODULE, toString());
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@Override
	@XmlTransient
	public String getStrPrefix() {
		return strPrefix;
	}
	@Override
	@XmlTransient
	public String[] getClassAttributeId() {
		return classAttributeId;
	}
	@Override
	@XmlTransient
	public List<KeyValuePair> getAttrbiutesFromReqeustPacket() {
		return attrbiutesFromReqeustPacket;
	}
	@Override
	@XmlTransient
	public List<KeyValuePair> getAttributesFromResponsePacket() {
		return attributesFromResponsePacket;
	}

	private List<KeyValuePair> getKeyValuePairArray(String[] stringArray) {
		ArrayList<KeyValuePair> keyValuePairList = new ArrayList<KeyValuePair>();
		if(stringArray!=null){
			int size = stringArray.length;
			for(int i=0;i<size;i++){
				KeyValuePair keyValuePair = new KeyValuePair();
				stringArray[i] = stringArray[i].trim();
				if(stringArray[i].contains("=")){
					int index = stringArray[i].indexOf("=");
					String key = stringArray[i].substring(0, index);
					String value = stringArray[i].substring(index+1);
					keyValuePair.setKey(key);
					if(value.startsWith("\"") && value.endsWith("\"")){
						value = value.substring(1, value.length()-1);
						keyValuePair.setDefaultValue(value);
					}else{
						keyValuePair.setAttributeId(value);
					}
				}else{
					keyValuePair.setKey(stringArray[i]);
					keyValuePair.setAttributeId(stringArray[i]);					
				}
				if(keyValuePair.getKey() != null && (keyValuePair.getAttributeId() != null || keyValuePair.getDefaultValue() != null))
				keyValuePairList.add(keyValuePair);
			}
		}
		return keyValuePairList;
	}
	
	private String[] getTrimedStringArray(String[] stringArray) {
		if(stringArray!=null){
			int size = stringArray.length;
			for(int i=0;i<size;i++){
				stringArray[i] = stringArray[i].trim();
			}
		}
		return stringArray;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("---VAS IN Class Attribute---");
		out.println("    Enabled 						= " + this.isEnabled);
		out.println("    Class Attribute Id 			= " + Arrays.toString(classAttributeId));
		out.println("    Separator						= "	+ this.strSeparator);
		out.println("    Attributes From Request Packet = " + this.requestPacketString);
		out.println("    Attributes From Response Packet= " + this.responsePacketString);
		out.close();
		return stringBuffer.toString();
	}
}
