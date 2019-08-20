package com.elitecore.elitesm.ws.rest.serverconfig.copypacketconfig;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
/**
 * To achieve XML readability for REST, this contains lists of request and response parameters for default mapping.
 */

@XmlRootElement(name  = "default-mapping")
public class DefaultMappingParameter {
	@Valid
	private List<CopyPacketTranslationMapDetailData> defaultRequestParameter;
	@Valid
	private List<CopyPacketTranslationMapDetailData> defaultResponseParameter; 
	
	
	public DefaultMappingParameter() {
		this.defaultRequestParameter = new LinkedList<CopyPacketTranslationMapDetailData>();
		this.defaultResponseParameter = new LinkedList<CopyPacketTranslationMapDetailData>();
	}

	@XmlElementWrapper(name = "request-parameters")
	@XmlElement(name = "request-parameter")
	public List<CopyPacketTranslationMapDetailData> getDefaultRequestParameter() {
		return defaultRequestParameter;
	}
	
	public void setDefaultRequestParameter(
			List<CopyPacketTranslationMapDetailData> defaultRequestParameter) {
		this.defaultRequestParameter = defaultRequestParameter;
	}
	
	@XmlElementWrapper(name = "response-parameters")
	@XmlElement(name = "response-parameter")
	public List<CopyPacketTranslationMapDetailData> getDefaultResponseParameter() {
		return defaultResponseParameter;
	}
	
	public void setDefaultResponseParameter(
			List<CopyPacketTranslationMapDetailData> defaultResponseParameter) {
		this.defaultResponseParameter = defaultResponseParameter;
	}

}
