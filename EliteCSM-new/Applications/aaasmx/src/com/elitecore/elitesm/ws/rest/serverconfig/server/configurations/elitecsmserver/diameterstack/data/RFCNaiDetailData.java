package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;


import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
@XmlType(propOrder = {"enabled","realmName"})
public class RFCNaiDetailData {

	@NotEmpty(message="Enable value must be specified for RFC-4282|RFC 5729 nai detail")
	private String enabled;
	private String realmName;
	public RFCNaiDetailData(){
	}
	
	@XmlElement(name = "enabled")
	@Pattern(regexp=RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid value of Enable(RFCNaiDetail) valid values are true/false")
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled.toLowerCase();
	}

	@XmlElement(name = "realm-names")
	public String getRealmName() {
		return realmName;
	}
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}	
}

