package com.elitecore.elitesm.ws.rest.serverconfig.translationmapping;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstDetailData;
@XmlRootElement(name  = "default-mapping")
/**
 * To achieve XML readability for REST, this contains lists of request and response parameters for default mapping.
 */
@XmlType(propOrder = {"defaultRequestParameter", "defaultResponseParameter"})
public class DefaultMappingParameter {
	
	private List<TranslationMappingInstDetailData> defaultRequestParameter = new LinkedList<TranslationMappingInstDetailData>();
	private List<TranslationMappingInstDetailData> defaultResponseParameter = new LinkedList<TranslationMappingInstDetailData>();
	
	@XmlElementWrapper(name = "request-parameters")
	@XmlElement(name = "request-parameter")
	public List<TranslationMappingInstDetailData> getDefaultRequestParameter() {
		return defaultRequestParameter;
	}
	
	public void setDefaultRequestParameter(
			List<TranslationMappingInstDetailData> defaultRequestParameter) {
		this.defaultRequestParameter = defaultRequestParameter;
	}
	
	@XmlElementWrapper(name = "response-parameters")
	@XmlElement(name = "response-parameter")
	public List<TranslationMappingInstDetailData> getDefaultResponseParameter() {
		return defaultResponseParameter;
	}
	
	public void setDefaultResponseParameter(
			List<TranslationMappingInstDetailData> defaultResponseParameter) {
		this.defaultResponseParameter = defaultResponseParameter;
	}

}
