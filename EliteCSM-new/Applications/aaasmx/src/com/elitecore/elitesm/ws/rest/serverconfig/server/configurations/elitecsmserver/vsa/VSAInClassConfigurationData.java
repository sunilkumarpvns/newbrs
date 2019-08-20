package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.vsa;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder = {"enabled","classAttributeIdString","separator","requestPacketString","responsePacketString"})
@XmlRootElement(name = "vsa-in-class-attribute")
public class VSAInClassConfigurationData {
	
	private String classAttributeIdString;
	@NotEmpty(message="Enabled parameter must be specified")
	private String enabled;
	private String separator;
	private String requestPacketString;
	private String responsePacketString;

	public VSAInClassConfigurationData() {
		 separator = ",";
	}
	
	@XmlElement(name = "separator")
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String stringOfSeparator) {
		this.separator = stringOfSeparator;
	}
	
	@XmlElement(name = "enabled")
	@Pattern(regexp=RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid value of Enabled (only true / false allow)")
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String isEnabled) {
		this.enabled = isEnabled.toLowerCase();
	}
	
	@XmlElement(name = "class-attribute-id")
	public String getClassAttributeIdString() {
		return classAttributeIdString;
	}
	public void setClassAttributeIdString(String classAttributeIdString) {
		this.classAttributeIdString = classAttributeIdString;
	}

	@XmlElement(name = "attributes-from-request-packet")
	public String getRequestPacketString() {
		return requestPacketString;
	}
	public void setRequestPacketString(String requestPacketString) {
		this.requestPacketString = requestPacketString;
	}
	
	@XmlElement(name = "attributes-from-response-packet")
	public String getResponsePacketString() {
		return responsePacketString;
	}
	public void setResponsePacketString(String responsePacketString) {
		this.responsePacketString = responsePacketString;
	}

}