package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder={})
public class GenericTranslationMappingDetailData{
	@NotEmpty(message="Diameter Generic Request Ws Mapping Name must be specified")
	private String translationMapping;
	
	public GenericTranslationMappingDetailData() {
	}

	@XmlElement(name="ws-mapping-name",type=String.class)
	public String getTranslationMapping() {
		if(Strings.isNullOrBlank(translationMapping)){
			return RestValidationMessages.NONE_WITH_HYPHEN;
		}
		return translationMapping;
	}

	public void setTranslationMapping(String translationMapping) {
		this.translationMapping = translationMapping;
	}
	
}